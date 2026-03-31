package com.example.hello.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 公告新增/编辑
 */
@Data
public class AnnouncementUpsertRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    /**
     * 过期时间，yyyy-MM-dd HH:mm:ss 或 ISO 格式；不传表示不过期
     */
    private String expireTime;

    /**
     * 置顶：0-否 1-是，默认 0
     */
    private Integer topStatus;

    /**
     * 状态：0-草稿 1-已发布；新增默认 0（草稿）
     */
    private Integer status;
}
