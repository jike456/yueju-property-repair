package com.example.hello.service.impl;

import com.example.hello.common.BusinessException;
import com.example.hello.dto.AdminAddUserRequest;
import com.example.hello.dto.UserLoginRequest;
import com.example.hello.dto.UserPageQueryResult;
import com.example.hello.dto.UserRegisterRequest;
import com.example.hello.entity.Owner;
import com.example.hello.entity.Repairman;
import com.example.hello.entity.User;
import com.example.hello.mapper.NotificationMapper;
import com.example.hello.mapper.OrderProcessMapper;
import com.example.hello.mapper.OwnerMapper;
import com.example.hello.mapper.RepairOrderMapper;
import com.example.hello.mapper.RepairmanMapper;
import com.example.hello.mapper.UserMapper;
import com.example.hello.security.JwtUtil;
import com.example.hello.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final OwnerMapper ownerMapper;
    private final RepairmanMapper repairmanMapper;
    private final NotificationMapper notificationMapper;
    private final RepairOrderMapper repairOrderMapper;
    private final OrderProcessMapper orderProcessMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper,
                           OwnerMapper ownerMapper,
                           RepairmanMapper repairmanMapper,
                           NotificationMapper notificationMapper,
                           RepairOrderMapper repairOrderMapper,
                           OrderProcessMapper orderProcessMapper,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.ownerMapper = ownerMapper;
        this.repairmanMapper = repairmanMapper;
        this.notificationMapper = notificationMapper;
        this.repairOrderMapper = repairOrderMapper;
        this.orderProcessMapper = orderProcessMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Long register(UserRegisterRequest request) {
        if (request.getRole() == null || (request.getRole() != 1 && request.getRole() != 2)) {
            throw new BusinessException("角色只能为业主或维修工");
        }
        validateUsernameAndPhone(request.getUsername(), request.getPhone());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setIdCard(request.getIdCard());
        user.setRole(request.getRole());
        user.setStatus(1);
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(null);
        user.setLastLoginTime(null);
        userMapper.insert(user);

        if (request.getRole() == 1) {
            if (!StringUtils.hasText(request.getBuildingNo()) || !StringUtils.hasText(request.getRoomNo())) {
                throw new BusinessException("业主注册时楼栋号和房间号必填");
            }
            Owner owner = new Owner();
            owner.setUserId(user.getId());
            owner.setBuildingNo(request.getBuildingNo());
            owner.setUnitNo(request.getUnitNo());
            owner.setRoomNo(request.getRoomNo());
            if (request.getArea() != null) {
                owner.setArea(BigDecimal.valueOf(request.getArea()));
            }
            if (StringUtils.hasText(request.getMoveInDate())) {
                owner.setMoveInDate(parseFlexibleLocalDate(request.getMoveInDate()));
            }
            owner.setCreateTime(now);
            ownerMapper.insert(owner);
        } else if (request.getRole() == 2) {
            Repairman repairman = new Repairman();
            repairman.setUserId(user.getId());
            repairman.setSkillType(request.getSkillType());
            repairman.setWorkYears(request.getWorkYears());
            repairman.setCreateTime(now);
            if (repairman.getWorkStatus() == null) {
                repairman.setWorkStatus(0);
            }
            if (repairman.getScore() == null) {
                repairman.setScore(new BigDecimal("5.00"));
            }
            if (repairman.getTotalOrders() == null) {
                repairman.setTotalOrders(0);
            }
            repairmanMapper.insert(repairman);
        }

        return user.getId();
    }

    @Override
    public Object login(UserLoginRequest request) {
        User user = userMapper.selectByUsername(request.getAccount());
        if (user == null) {
            user = userMapper.selectByPhone(request.getAccount());
        }
        if (user == null) {
            throw new BusinessException("账号错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        user.setLastLoginTime(LocalDateTime.now());
        userMapper.update(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("avatar", user.getAvatar());
        return data;
    }

    @Override
    public UserPageQueryResult pageQuery(Integer page, Integer pageSize, Integer role, Integer status, String keyword) {
        int pageIndex = (page == null || page < 1) ? 1 : page;
        int size = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        String kw = StringUtils.hasText(keyword) ? keyword.trim() : null;

        PageHelper.startPage(pageIndex, size);
        java.util.List<User> userList = userMapper.selectByCondition(role, status, kw);
        PageInfo<User> pageInfo = new PageInfo<>(userList);

        UserPageQueryResult result = new UserPageQueryResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(userList.stream().map(u -> {
            UserPageQueryResult.UserItem item = new UserPageQueryResult.UserItem();
            item.setId(u.getId());
            item.setUsername(u.getUsername());
            item.setRealName(u.getRealName());
            item.setPhone(u.getPhone());
            item.setRole(u.getRole());
            item.setStatus(u.getStatus());
            item.setCreateTime(u.getCreateTime());
            item.setLastLoginTime(u.getLastLoginTime());
            if (u.getRole() != null && u.getRole() == 1) {
                Owner o = ownerMapper.selectByUserId(u.getId());
                if (o != null) {
                    UserPageQueryResult.OwnerInfo ownerInfo = new UserPageQueryResult.OwnerInfo();
                    ownerInfo.setBuildingNo(o.getBuildingNo());
                    ownerInfo.setUnitNo(o.getUnitNo());
                    ownerInfo.setRoomNo(o.getRoomNo());
                    if (o.getArea() != null) {
                        ownerInfo.setArea(o.getArea().doubleValue());
                    }
                    item.setOwnerInfo(ownerInfo);
                }
            } else if (u.getRole() != null && u.getRole() == 2) {
                Repairman r = repairmanMapper.selectByUserId(u.getId());
                if (r != null) {
                    UserPageQueryResult.RepairmanInfo repairmanInfo = new UserPageQueryResult.RepairmanInfo();
                    repairmanInfo.setSkillType(r.getSkillType());
                    repairmanInfo.setWorkYears(r.getWorkYears());
                    item.setRepairmanInfo(repairmanInfo);
                }
            }
            return item;
        }).collect(Collectors.toList()));
        return result;
    }

    @Override
    public Long addUser(AdminAddUserRequest request, Long operatorUserId, Integer operatorRole) {
        if (operatorRole == null || operatorRole != 3) {
            throw new BusinessException("只有管理员可以添加用户");
        }
        if (request.getRole() == null || request.getRole() < 1 || request.getRole() > 3) {
            throw new BusinessException("角色不合法");
        }
        validateUsernameAndPhone(request.getUsername(), request.getPhone());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setIdCard(request.getIdCard());
        user.setRole(request.getRole());
        user.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(null);
        user.setLastLoginTime(null);
        userMapper.insert(user);

        if (request.getRole() == 1) {
            if (!StringUtils.hasText(request.getBuildingNo()) || !StringUtils.hasText(request.getRoomNo())) {
                throw new BusinessException("添加业主时楼栋号和房间号必填");
            }
            Owner owner = new Owner();
            owner.setUserId(user.getId());
            owner.setBuildingNo(request.getBuildingNo());
            owner.setUnitNo(request.getUnitNo());
            owner.setRoomNo(request.getRoomNo());
            if (request.getArea() != null) {
                owner.setArea(BigDecimal.valueOf(request.getArea()));
            }
            if (StringUtils.hasText(request.getMoveInDate())) {
                owner.setMoveInDate(parseFlexibleLocalDate(request.getMoveInDate()));
            }
            owner.setCreateTime(now);
            ownerMapper.insert(owner);
        } else if (request.getRole() == 2) {
            Repairman repairman = new Repairman();
            repairman.setUserId(user.getId());
            repairman.setSkillType(request.getSkillType());
            repairman.setWorkYears(request.getWorkYears());
            repairman.setCreateTime(now);
            if (repairman.getWorkStatus() == null) {
                repairman.setWorkStatus(0);
            }
            if (repairman.getScore() == null) {
                repairman.setScore(new BigDecimal("5.00"));
            }
            if (repairman.getTotalOrders() == null) {
                repairman.setTotalOrders(0);
            }
            repairmanMapper.insert(repairman);
        }
        return user.getId();
    }

    @Override
    public void updateStatus(Long userId, Integer status, Long operatorUserId, Integer operatorRole) {
        if (operatorRole == null || operatorRole != 3) {
            throw new BusinessException("只有管理员可以修改用户状态");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态不合法");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, Long operatorUserId, Integer operatorRole) {
        if (operatorRole == null || operatorRole != 3) {
            throw new BusinessException("只有管理员可以删除用户");
        }
        if (operatorUserId == null || userId.equals(operatorUserId)) {
            throw new BusinessException("不能删除自己的账号");
        }
        User target = userMapper.selectById(userId);
        if (target == null) {
            throw new BusinessException("用户不存在");
        }
        Integer role = target.getRole();
        if (role != null && role == 3) {
            int otherAdmins = userMapper.countByRoleExcludingId(3, userId);
            if (otherAdmins == 0) {
                throw new BusinessException("不能删除最后一个管理员");
            }
        }
        if (role != null && role == 1) {
            Owner owner = ownerMapper.selectByUserId(userId);
            if (owner != null && repairOrderMapper.countByOwnerId(owner.getId()) > 0) {
                throw new BusinessException("该业主存在报修工单，无法删除");
            }
        }
        if (role != null && role == 2) {
            Repairman repairman = repairmanMapper.selectByUserId(userId);
            if (repairman != null && orderProcessMapper.countByRepairmanId(repairman.getId()) > 0) {
                throw new BusinessException("该维修工存在关联工单，无法删除");
            }
        }

        notificationMapper.deleteByReceiverOrSender(userId);
        if (role != null && role == 3) {
            orderProcessMapper.clearAssignerByUserId(userId);
        }
        if (role != null && role == 1) {
            ownerMapper.deleteByUserId(userId);
        }
        if (role != null && role == 2) {
            repairmanMapper.deleteByUserId(userId);
        }
        int n = userMapper.deleteById(userId);
        if (n == 0) {
            throw new BusinessException("删除失败");
        }
    }

    /**
     * 支持 yyyy-MM-dd、ISO-8601 日期时间（如 Element Plus 日期控件返回的 2026-03-17T16:00:00.000Z）
     */
    private LocalDate parseFlexibleLocalDate(String raw) {
        String s = raw.trim();
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException ignored) {
        }
        if (s.length() >= 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
            try {
                return LocalDate.parse(s.substring(0, 10));
            } catch (DateTimeParseException ignored) {
            }
        }
        try {
            return Instant.parse(s).atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (DateTimeParseException ignored) {
        }
        throw new BusinessException("入住日期格式无效");
    }

    private void validateUsernameAndPhone(String username, String phone) {
        if (userMapper.countByUsername(username) > 0) {
            throw new BusinessException("用户名已存在");
        }
        if (userMapper.countByPhone(phone) > 0) {
            throw new BusinessException("手机号已存在");
        }
    }
}

