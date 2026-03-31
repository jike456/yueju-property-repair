package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegisterRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 角色：1-业主 2-维修工
     */
    @NotNull(message = "角色不能为空")
    private Integer role;

    private String idCard;

    // 业主信息
    private String buildingNo;
    private String unitNo;
    private String roomNo;
    private Double area;
    private String moveInDate;

    // 维修工信息
    private String skillType;
    private Integer workYears;
}

