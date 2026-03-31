package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.dto.*;
import com.example.hello.security.LoginUser;
import com.example.hello.service.RepairOrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    public RepairOrderController(RepairOrderService repairOrderService) {
        this.repairOrderService = repairOrderService;
    }

    /**
     * 4.1 业主提交报修
     */
    @PostMapping
    public ApiResponse<CreateOrderResult> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        LoginUser current = getCurrentUser();
        CreateOrderResult result = repairOrderService.createOrder(request, current.getUserId(), current.getRole());
        return ApiResponse.success(result);
    }

    /**
     * 4.2 业主分页查询自己的工单
     */
    @GetMapping("/my")
    public ApiResponse<OrderPageResult> pageQueryMy(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        LoginUser current = getCurrentUser();
        OrderPageResult result = repairOrderService.pageQueryMy(page, pageSize, status, current.getUserId());
        return ApiResponse.success(result);
    }

    /**
     * 4.3 维修工分页查询本人工单（已派单给当前维修工）
     */
    @GetMapping("/repairman")
    public ApiResponse<OrderPageResult> pageQueryRepairman(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        LoginUser current = getCurrentUser();
        OrderPageResult result = repairOrderService.pageQueryRepairman(
                page, pageSize, status, priority, startTime, endTime,
                current.getUserId(), current.getRole());
        return ApiResponse.success(result);
    }

    /**
     * 4.4 管理员分页查询所有工单
     */
    @GetMapping
    public ApiResponse<OrderPageResult> pageQueryAdmin(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long repairmanId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        LoginUser current = getCurrentUser();
        OrderPageResult result = repairOrderService.pageQueryAdmin(
                page, pageSize, status, priority, ownerId, repairmanId, startTime, endTime,
                current.getUserId(), current.getRole());
        return ApiResponse.success(result);
    }

    /**
     * 4.5 获取工单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderDetailResult> getDetail(@PathVariable("id") Long id) {
        OrderDetailResult result = repairOrderService.getDetail(id);
        return ApiResponse.success(result);
    }

    /**
     * 4.6 管理员派单
     */
    @PostMapping("/{id}/dispatch")
    public ApiResponse<Void> dispatch(@PathVariable("id") Long id,
                                      @Valid @RequestBody DispatchOrderRequest request) {
        LoginUser current = getCurrentUser();
        repairOrderService.dispatch(id, request, current.getUserId(), current.getRole());
        return ApiResponse.success(null);
    }

    /**
     * 4.7 维修工接单
     */
    @PostMapping("/{id}/accept")
    public ApiResponse<Void> accept(@PathVariable("id") Long id) {
        LoginUser current = getCurrentUser();
        repairOrderService.accept(id, current.getUserId(), current.getRole());
        return ApiResponse.success(null);
    }

    /**
     * 4.8 维修工开始维修
     */
    @PostMapping("/{id}/start")
    public ApiResponse<Void> start(@PathVariable("id") Long id) {
        LoginUser current = getCurrentUser();
        repairOrderService.start(id, current.getUserId(), current.getRole());
        return ApiResponse.success(null);
    }

    /**
     * 4.9 维修工完成维修
     */
    @PostMapping("/{id}/complete")
    public ApiResponse<Void> complete(@PathVariable("id") Long id,
                                     @RequestBody(required = false) CompleteOrderRequest request) {
        LoginUser current = getCurrentUser();
        repairOrderService.complete(id, request != null ? request : new CompleteOrderRequest(),
                current.getUserId(), current.getRole());
        return ApiResponse.success(null);
    }

    /**
     * 4.10 取消工单
     */
    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable("id") Long id,
                                   @RequestBody(required = false) CancelOrderRequest request) {
        LoginUser current = getCurrentUser();
        repairOrderService.cancel(id, request != null ? request : new CancelOrderRequest(),
                current.getUserId(), current.getRole());
        return ApiResponse.success(null);
    }

    /**
     * 5.1 提交评价
     */
    @PostMapping("/{orderId}/evaluation")
    public ApiResponse<java.util.Map<String, Object>> submitEvaluation(
            @PathVariable("orderId") Long orderId,
            @Valid @RequestBody SubmitEvaluationRequest request) {
        LoginUser current = getCurrentUser();
        Long evalId = repairOrderService.submitEvaluation(orderId, request, current.getUserId(), current.getRole());
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("evaluationId", evalId);
        return ApiResponse.success("评价成功", data);
    }

    /**
     * 5.2 获取评价详情
     */
    @GetMapping("/{orderId}/evaluation")
    public ApiResponse<EvaluationDetailResult> getEvaluation(@PathVariable("orderId") Long orderId) {
        EvaluationDetailResult result = repairOrderService.getEvaluation(orderId);
        return ApiResponse.success(result);
    }

    private LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            return new LoginUser(null, null, null);
        }
        return (LoginUser) authentication.getPrincipal();
    }
}
