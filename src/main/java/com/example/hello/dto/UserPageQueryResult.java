package com.example.hello.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserPageQueryResult {

    private long total;
    private List<UserItem> rows;

    @Data
    public static class UserItem {
        private Long id;
        private String username;
        private String realName;
        private String phone;
        private Integer role;
        private Integer status;
        private LocalDateTime createTime;
        private LocalDateTime lastLoginTime;
        private OwnerInfo ownerInfo;
        private RepairmanInfo repairmanInfo;
    }

    @Data
    public static class OwnerInfo {
        private String buildingNo;
        private String unitNo;
        private String roomNo;
        private Double area;
    }

    @Data
    public static class RepairmanInfo {
        private String skillType;
        private Integer workYears;
    }
}

