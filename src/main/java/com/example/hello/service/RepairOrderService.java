package com.example.hello.service;

import com.example.hello.dto.*;

public interface RepairOrderService {

    CreateOrderResult createOrder(CreateOrderRequest request, Long currentUserId, Integer currentUserRole);

    OrderPageResult pageQueryMy(Integer page, Integer pageSize, Integer status, Long currentUserId);

    OrderPageResult pageQueryAdmin(Integer page, Integer pageSize, Integer status, Integer priority,
                                   Long ownerId, Long repairmanId, String startTime, String endTime,
                                   Long currentUserId, Integer currentUserRole);

    /**
     * 维修工分页查询本人相关工单（已派单给当前维修工）
     */
    OrderPageResult pageQueryRepairman(Integer page, Integer pageSize, Integer status, Integer priority,
                                       String startTime, String endTime,
                                       Long currentUserId, Integer currentUserRole);

    OrderDetailResult getDetail(Long orderId);

    void dispatch(Long orderId, DispatchOrderRequest request, Long currentUserId, Integer currentUserRole);

    void accept(Long orderId, Long currentUserId, Integer currentUserRole);

    void start(Long orderId, Long currentUserId, Integer currentUserRole);

    void complete(Long orderId, CompleteOrderRequest request, Long currentUserId, Integer currentUserRole);

    void cancel(Long orderId, CancelOrderRequest request, Long currentUserId, Integer currentUserRole);

    Long submitEvaluation(Long orderId, SubmitEvaluationRequest request,
                          Long currentUserId, Integer currentUserRole);

    EvaluationDetailResult getEvaluation(Long orderId);
}
