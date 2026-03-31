package com.example.hello.mapper;

import com.example.hello.entity.RepairOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RepairOrderMapper {

    int insert(RepairOrder order);

    int update(RepairOrder order);

    RepairOrder selectById(@Param("id") Long id);

    RepairOrder selectByOrderNo(@Param("orderNo") String orderNo);

    List<RepairOrder> selectByOwnerId(@Param("ownerId") Long ownerId,
                                     @Param("status") Integer status);

    List<RepairOrder> selectByCondition(@Param("status") Integer status,
                                        @Param("priority") Integer priority,
                                        @Param("ownerId") Long ownerId,
                                        @Param("repairmanId") Long repairmanId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    String selectMaxOrderNoByDate(@Param("datePrefix") String datePrefix);

    int countByOwnerId(@Param("ownerId") Long ownerId);
}
