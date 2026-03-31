package com.example.hello.service.impl;

import com.example.hello.common.BusinessException;
import com.example.hello.dto.*;
import com.example.hello.entity.*;
import com.example.hello.mapper.*;
import com.example.hello.service.RepairOrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepairOrderServiceImpl implements RepairOrderService {

    private final RepairOrderMapper repairOrderMapper;
    private final OrderProcessMapper orderProcessMapper;
    private final OrderEvaluationMapper orderEvaluationMapper;
    private final OwnerMapper ownerMapper;
    private final RepairmanMapper repairmanMapper;
    private final UserMapper userMapper;
    private final FaultTypeMapper faultTypeMapper;
    private final ObjectMapper objectMapper;

    public RepairOrderServiceImpl(RepairOrderMapper repairOrderMapper,
                                  OrderProcessMapper orderProcessMapper,
                                  OrderEvaluationMapper orderEvaluationMapper,
                                  OwnerMapper ownerMapper,
                                  RepairmanMapper repairmanMapper,
                                  UserMapper userMapper,
                                  FaultTypeMapper faultTypeMapper,
                                  ObjectMapper objectMapper) {
        this.repairOrderMapper = repairOrderMapper;
        this.orderProcessMapper = orderProcessMapper;
        this.orderEvaluationMapper = orderEvaluationMapper;
        this.ownerMapper = ownerMapper;
        this.repairmanMapper = repairmanMapper;
        this.userMapper = userMapper;
        this.faultTypeMapper = faultTypeMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public CreateOrderResult createOrder(CreateOrderRequest request, Long currentUserId, Integer currentUserRole) {
        if (currentUserRole == null || currentUserRole != 1) {
            throw new BusinessException("只有业主可以提交报修");
        }
        Owner owner = ownerMapper.selectByUserId(currentUserId);
        if (owner == null) {
            throw new BusinessException("业主信息不存在");
        }
        FaultType faultType = faultTypeMapper.selectById(request.getFaultTypeId());
        if (faultType == null) {
            throw new BusinessException("故障类型不存在");
        }
        if (faultType.getLevel() != null && faultType.getLevel() != 2) {
            throw new BusinessException("请选择二级故障类型");
        }

        String addressDetail = request.getAddressDetail();
        if (!StringUtils.hasText(addressDetail)) {
            addressDetail = buildAddress(owner);
        }

        String orderNo = generateOrderNo();
        RepairOrder order = new RepairOrder();
        order.setOrderNo(orderNo);
        order.setOwnerId(owner.getId());
        order.setFaultTypeId(request.getFaultTypeId());
        order.setTitle(request.getTitle());
        order.setDescription(request.getDescription());
        order.setImages(toJson(request.getImages()));
        order.setAddressDetail(addressDetail);
        order.setStatus(0);
        order.setPriority(request.getPriority() != null ? request.getPriority() : 2);
        LocalDateTime now = LocalDateTime.now();
        order.setCreateTime(now);
        if (StringUtils.hasText(request.getAppointmentTime())) {
            try {
                order.setAppointmentTime(LocalDateTime.parse(request.getAppointmentTime().replace(" ", "T")));
            } catch (Exception ignored) {
            }
        }

        repairOrderMapper.insert(order);

        CreateOrderResult result = new CreateOrderResult();
        result.setOrderId(order.getId());
        result.setOrderNo(order.getOrderNo());
        return result;
    }

    @Override
    public OrderPageResult pageQueryMy(Integer page, Integer pageSize, Integer status, Long currentUserId) {
        if (currentUserId == null) {
            throw new BusinessException("请先登录");
        }
        Owner owner = ownerMapper.selectByUserId(currentUserId);
        if (owner == null) {
            throw new BusinessException("业主信息不存在");
        }

        int pageIndex = (page == null || page < 1) ? 1 : page;
        int size = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        PageHelper.startPage(pageIndex, size);
        List<RepairOrder> list = repairOrderMapper.selectByOwnerId(owner.getId(), status);
        PageInfo<RepairOrder> pageInfo = new PageInfo<>(list);

        OrderPageResult result = new OrderPageResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(toOrderItems(list, true));
        return result;
    }

    @Override
    public OrderPageResult pageQueryAdmin(Integer page, Integer pageSize, Integer status, Integer priority,
                                          Long ownerId, Long repairmanId, String startTime, String endTime,
                                          Long currentUserId, Integer currentUserRole) {
        if (currentUserRole == null || currentUserRole != 3) {
            throw new BusinessException("只有管理员可以查询所有工单");
        }
        LocalDateTime start = parseDateTime(startTime);
        LocalDateTime end = parseDateTime(endTime);

        int pageIndex = (page == null || page < 1) ? 1 : page;
        int size = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        PageHelper.startPage(pageIndex, size);
        List<RepairOrder> list = repairOrderMapper.selectByCondition(status, priority, ownerId, repairmanId, start, end);
        PageInfo<RepairOrder> pageInfo = new PageInfo<>(list);

        OrderPageResult result = new OrderPageResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(toOrderItems(list, false));
        return result;
    }

    @Override
    public OrderPageResult pageQueryRepairman(Integer page, Integer pageSize, Integer status, Integer priority,
                                              String startTime, String endTime,
                                              Long currentUserId, Integer currentUserRole) {
        if (currentUserRole == null || currentUserRole != 2) {
            throw new BusinessException("只有维修工可以查询本人工单");
        }
        Repairman repairman = repairmanMapper.selectByUserId(currentUserId);
        if (repairman == null) {
            throw new BusinessException("维修工信息不存在");
        }
        LocalDateTime start = parseDateTime(startTime);
        LocalDateTime end = parseDateTime(endTime);

        int pageIndex = (page == null || page < 1) ? 1 : page;
        int size = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        PageHelper.startPage(pageIndex, size);
        List<RepairOrder> list = repairOrderMapper.selectByCondition(status, priority, null, repairman.getId(), start, end);
        PageInfo<RepairOrder> pageInfo = new PageInfo<>(list);

        OrderPageResult result = new OrderPageResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(toOrderItems(list, false));
        return result;
    }

    @Override
    public OrderDetailResult getDetail(Long orderId) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }

        OrderDetailResult result = new OrderDetailResult();
        result.setId(order.getId());
        result.setOrderNo(order.getOrderNo());
        result.setTitle(order.getTitle());
        result.setDescription(order.getDescription());
        result.setImages(parseJsonList(order.getImages()));
        result.setStatus(order.getStatus());
        result.setPriority(order.getPriority());
        result.setCreateTime(order.getCreateTime());
        result.setAppointmentTime(order.getAppointmentTime());
        result.setAddressDetail(order.getAddressDetail());

        FaultType ft = faultTypeMapper.selectById(order.getFaultTypeId());
        result.setFaultTypeName(ft != null ? ft.getTypeName() : null);

        Owner owner = ownerMapper.selectById(order.getOwnerId());
        if (owner != null) {
            User ownerUser = userMapper.selectById(owner.getUserId());
            OrderDetailResult.OwnerInfo oi = new OrderDetailResult.OwnerInfo();
            oi.setRealName(ownerUser != null ? ownerUser.getRealName() : null);
            oi.setPhone(ownerUser != null ? ownerUser.getPhone() : null);
            oi.setAddress(buildAddress(owner));
            result.setOwner(oi);
        }

        OrderProcess process = orderProcessMapper.selectByOrderId(orderId);
        if (process != null) {
            OrderDetailResult.ProcessInfo pi = new OrderDetailResult.ProcessInfo();
            pi.setAssignTime(process.getAssignTime());
            pi.setAcceptTime(process.getAcceptTime());
            pi.setStartTime(process.getStartTime());
            pi.setEndTime(process.getEndTime());
            pi.setProcessNote(process.getProcessNote());
            pi.setProcessImages(parseJsonList(process.getProcessImages()));
            pi.setMaterialUsed(process.getMaterialUsed());
            if (process.getRepairmanId() != null) {
                Repairman r = repairmanMapper.selectById(process.getRepairmanId());
                if (r != null) {
                    User ru = userMapper.selectById(r.getUserId());
                    pi.setRepairmanName(ru != null ? ru.getRealName() : null);
                }
            }
            result.setProcess(pi);
        }

        OrderEvaluation eval = orderEvaluationMapper.selectByOrderId(orderId);
        if (eval != null) {
            OrderDetailResult.EvaluationInfo ei = new OrderDetailResult.EvaluationInfo();
            ei.setScore(eval.getScore());
            ei.setContent(eval.getContent());
            ei.setImages(parseJsonList(eval.getImages()));
            ei.setCreateTime(eval.getCreateTime());
            result.setEvaluation(ei);
        }

        return result;
    }

    @Override
    @Transactional
    public void dispatch(Long orderId, DispatchOrderRequest request, Long currentUserId, Integer currentUserRole) {
        if (currentUserRole == null || currentUserRole != 3) {
            throw new BusinessException("只有管理员可以派单");
        }
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("工单状态不允许派单");
        }
        Repairman repairman = repairmanMapper.selectById(request.getRepairmanId());
        if (repairman == null) {
            throw new BusinessException("维修工不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        OrderProcess process = orderProcessMapper.selectByOrderId(orderId);
        if (process == null) {
            process = new OrderProcess();
            process.setOrderId(orderId);
            process.setRepairmanId(request.getRepairmanId());
            process.setAssignerId(currentUserId);
            process.setAssignTime(now);
            process.setCreateTime(now);
            orderProcessMapper.insert(process);
        } else {
            process.setRepairmanId(request.getRepairmanId());
            process.setAssignerId(currentUserId);
            process.setAssignTime(now);
            orderProcessMapper.update(process);
        }

        order.setStatus(1);
        order.setUpdateTime(now);
        repairOrderMapper.update(order);
    }

    @Override
    @Transactional
    public void accept(Long orderId, Long currentUserId, Integer currentUserRole) {
        if (currentUserRole == null || currentUserRole != 2) {
            throw new BusinessException("只有维修工可以接单");
        }
        Repairman repairman = repairmanMapper.selectByUserId(currentUserId);
        if (repairman == null) {
            throw new BusinessException("维修工信息不存在");
        }
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("工单状态不允许接单");
        }
        OrderProcess process = orderProcessMapper.selectByOrderId(orderId);
        if (process == null || !repairman.getId().equals(process.getRepairmanId())) {
            throw new BusinessException("该工单未派给您");
        }

        LocalDateTime now = LocalDateTime.now();
        process.setAcceptTime(now);
        orderProcessMapper.update(process);
        order.setStatus(2);
        order.setUpdateTime(now);
        repairOrderMapper.update(order);
    }

    @Override
    @Transactional
    public void start(Long orderId, Long currentUserId, Integer currentUserRole) {
        if (currentUserRole == null || currentUserRole != 2) {
            throw new BusinessException("只有维修工可以开始维修");
        }
        Repairman repairman = repairmanMapper.selectByUserId(currentUserId);
        if (repairman == null) {
            throw new BusinessException("维修工信息不存在");
        }
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("工单状态不允许开始维修");
        }
        OrderProcess process = orderProcessMapper.selectByOrderId(orderId);
        if (process == null || !repairman.getId().equals(process.getRepairmanId())) {
            throw new BusinessException("该工单未派给您");
        }

        LocalDateTime now = LocalDateTime.now();
        process.setStartTime(now);
        orderProcessMapper.update(process);
    }

    @Override
    @Transactional
    public void complete(Long orderId, CompleteOrderRequest request, Long currentUserId, Integer currentUserRole) {
        if (currentUserRole == null || currentUserRole != 2) {
            throw new BusinessException("只有维修工可以完成维修");
        }
        Repairman repairman = repairmanMapper.selectByUserId(currentUserId);
        if (repairman == null) {
            throw new BusinessException("维修工信息不存在");
        }
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("工单状态不允许完成");
        }
        OrderProcess process = orderProcessMapper.selectByOrderId(orderId);
        if (process == null || !repairman.getId().equals(process.getRepairmanId())) {
            throw new BusinessException("该工单未派给您");
        }

        LocalDateTime now = LocalDateTime.now();
        process.setEndTime(now);
        process.setProcessNote(request != null ? request.getProcessNote() : null);
        process.setProcessImages(request != null ? toJson(request.getProcessImages()) : null);
        process.setMaterialUsed(request != null ? request.getMaterialUsed() : null);
        orderProcessMapper.update(process);

        order.setStatus(3);
        order.setUpdateTime(now);
        repairOrderMapper.update(order);
    }

    @Override
    @Transactional
    public void cancel(Long orderId, CancelOrderRequest request, Long currentUserId, Integer currentUserRole) {
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        boolean canCancel = false;
        if (currentUserRole != null && currentUserRole == 3) {
            canCancel = true;
        } else if (currentUserRole != null && currentUserRole == 1) {
            Owner owner = ownerMapper.selectByUserId(currentUserId);
            if (owner != null && owner.getId().equals(order.getOwnerId())) {
                canCancel = true;
            }
        }
        if (!canCancel) {
            throw new BusinessException("无权限取消该工单");
        }
        if (order.getStatus() == 4 || order.getStatus() == 5 || order.getStatus() == 6) {
            throw new BusinessException("工单状态不允许取消");
        }

        order.setStatus(5);
        order.setUpdateTime(LocalDateTime.now());
        repairOrderMapper.update(order);
    }

    @Override
    @Transactional
    public Long submitEvaluation(Long orderId, SubmitEvaluationRequest request, Long currentUserId, Integer currentUserRole) {
        if (currentUserRole == null || currentUserRole != 1) {
            throw new BusinessException("只有业主可以提交评价");
        }
        Owner owner = ownerMapper.selectByUserId(currentUserId);
        if (owner == null) {
            throw new BusinessException("业主信息不存在");
        }
        RepairOrder order = repairOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (!owner.getId().equals(order.getOwnerId())) {
            throw new BusinessException("只能评价自己的工单");
        }
        if (order.getStatus() != 3 && order.getStatus() != 4) {
            throw new BusinessException("只有待确认或已完成的工单可以评价");
        }
        OrderEvaluation existing = orderEvaluationMapper.selectByOrderId(orderId);
        if (existing != null) {
            throw new BusinessException("该工单已评价");
        }
        OrderProcess process = orderProcessMapper.selectByOrderId(orderId);
        if (process == null || process.getRepairmanId() == null) {
            throw new BusinessException("工单尚未派单，无法评价");
        }

        OrderEvaluation evaluation = new OrderEvaluation();
        evaluation.setOrderId(orderId);
        evaluation.setOwnerId(owner.getId());
        evaluation.setRepairmanId(process.getRepairmanId());
        evaluation.setScore(request.getScore());
        evaluation.setContent(request.getContent());
        evaluation.setImages(toJson(request.getImages()));
        evaluation.setAnonymous(Boolean.TRUE.equals(request.getAnonymous()) ? 1 : 0);
        evaluation.setCreateTime(LocalDateTime.now());
        orderEvaluationMapper.insert(evaluation);

        order.setStatus(4);
        order.setUpdateTime(LocalDateTime.now());
        repairOrderMapper.update(order);

        return evaluation.getId();
    }

    @Override
    public EvaluationDetailResult getEvaluation(Long orderId) {
        OrderEvaluation eval = orderEvaluationMapper.selectByOrderId(orderId);
        if (eval == null) {
            return null;
        }
        EvaluationDetailResult result = new EvaluationDetailResult();
        result.setId(eval.getId());
        result.setScore(eval.getScore());
        result.setContent(eval.getContent());
        result.setImages(parseJsonList(eval.getImages()));
        result.setCreateTime(eval.getCreateTime());
        if (eval.getAnonymous() == null || eval.getAnonymous() == 0) {
            Owner owner = ownerMapper.selectById(eval.getOwnerId());
            if (owner != null) {
                User ownerUser = userMapper.selectById(owner.getUserId());
                result.setOwnerName(ownerUser != null ? ownerUser.getRealName() : null);
            }
        }
        return result;
    }

    private String generateOrderNo() {
        String datePrefix = "YX" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String max = repairOrderMapper.selectMaxOrderNoByDate(datePrefix);
        int seq = 1;
        if (StringUtils.hasText(max) && max.length() >= 14) {
            try {
                seq = Integer.parseInt(max.substring(10)) + 1;
            } catch (Exception ignored) {
            }
        }
        return datePrefix + String.format("%06d", seq);
    }

    private String buildAddress(Owner owner) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(owner.getBuildingNo())) sb.append(owner.getBuildingNo());
        if (StringUtils.hasText(owner.getUnitNo())) sb.append(owner.getUnitNo()).append("单元");
        if (StringUtils.hasText(owner.getRoomNo())) sb.append(owner.getRoomNo()).append("室");
        return sb.toString();
    }

    private List<OrderPageResult.OrderItem> toOrderItems(List<RepairOrder> list, boolean forOwner) {
        List<OrderPageResult.OrderItem> items = new ArrayList<>();
        for (RepairOrder o : list) {
            OrderPageResult.OrderItem item = new OrderPageResult.OrderItem();
            item.setId(o.getId());
            item.setOrderNo(o.getOrderNo());
            item.setTitle(o.getTitle());
            item.setStatus(o.getStatus());
            item.setPriority(o.getPriority());
            item.setCreateTime(o.getCreateTime());
            item.setAppointmentTime(o.getAppointmentTime());

            FaultType ft = faultTypeMapper.selectById(o.getFaultTypeId());
            item.setFaultTypeName(ft != null ? ft.getTypeName() : null);

            if (!forOwner) {
                Owner owner = ownerMapper.selectById(o.getOwnerId());
                if (owner != null) {
                    User ou = userMapper.selectById(owner.getUserId());
                    item.setOwnerName(ou != null ? ou.getRealName() : null);
                }
            }

            OrderProcess process = orderProcessMapper.selectByOrderId(o.getId());
            if (process != null && process.getRepairmanId() != null) {
                Repairman r = repairmanMapper.selectById(process.getRepairmanId());
                if (r != null) {
                    User ru = userMapper.selectById(r.getUserId());
                    item.setRepairmanName(ru != null ? ru.getRealName() : null);
                }
            }
            items.add(item);
        }
        return items;
    }

    private LocalDateTime parseDateTime(String str) {
        if (!StringUtils.hasText(str)) return null;
        try {
            if (str.length() <= 10) {
                return LocalDate.parse(str).atStartOfDay();
            }
            return LocalDateTime.parse(str.replace(" ", "T"));
        } catch (Exception e) {
            return null;
        }
    }

    private String toJson(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> parseJsonList(String json) {
        if (!StringUtils.hasText(json)) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
