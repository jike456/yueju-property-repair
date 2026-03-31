package com.example.hello.controller;

import com.example.hello.common.ApiResponse;
import com.example.hello.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 文件上传接口
 * POST /api/files/upload
 * 请求：multipart/form-data，字段名 file
 * 响应：{ "code": 1, "data": { "url": "https://xxx/uploads/2025/03/04/uuid.jpg" } }
 * <p>
 * 删除：DELETE /api/files?url= 上传接口返回的 url（完整或 /uploads/...）
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final Pattern UPLOAD_FILE_NAME = Pattern.compile("(?i)[0-9a-f]{32}\\.(jpg|jpeg|png|gif|webp)");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXT = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.base-url:}")
    private String baseUrl;

    @PostMapping("/upload")
    public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("文件大小不能超过 5MB");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isEmpty()) {
            throw new BusinessException("文件名无效");
        }
        String ext = getExtension(originalName);
        if (!isAllowedExt(ext)) {
            throw new BusinessException("仅支持 jpg、png、gif、webp 格式");
        }

        String relativePath = saveFile(file, ext);
        String url = buildFullUrl(relativePath);

        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return ApiResponse.success(data);
    }

    /**
     * 删除已上传文件（仅允许删除本服务 uploads 目录下、符合命名规则的文件）
     */
    @DeleteMapping
    public ApiResponse<Void> delete(@RequestParam("url") String url) {
        if (!StringUtils.hasText(url)) {
            throw new BusinessException("请提供文件地址 url");
        }
        String decoded;
        try {
            decoded = URLDecoder.decode(url.trim(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            decoded = url.trim();
        }
        Path target = resolveSafeUploadFile(decoded);
        try {
            if (!Files.isRegularFile(target)) {
                return ApiResponse.success("文件已不存在", null);
            }
            Files.delete(target);
            return ApiResponse.success("已删除", null);
        } catch (IOException e) {
            throw new BusinessException("删除失败: " + e.getMessage());
        }
    }

    private String saveFile(MultipartFile file, String ext) {
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        String relativePath = "uploads/" + dateDir + "/" + fileName;

        try {
            Path baseDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path dir = baseDir.resolve(dateDir);
            Files.createDirectories(dir);
            Path target = dir.resolve(fileName);
            file.transferTo(target.toFile());
        } catch (IOException e) {
            throw new BusinessException("文件保存失败: " + e.getMessage());
        }
        return "/" + relativePath;
    }

    private String buildFullUrl(String relativePath) {
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            return base + relativePath;
        }
        return relativePath;
    }

    private String getExtension(String filename) {
        int i = filename.lastIndexOf('.');
        return i > 0 ? filename.substring(i).toLowerCase() : "";
    }

    private boolean isAllowedExt(String ext) {
        for (String e : ALLOWED_EXT) {
            if (e.equalsIgnoreCase(ext)) return true;
        }
        return false;
    }

    /**
     * 将完整 URL 或 /uploads/yyyy/MM/dd/name.ext 解析为上传目录下的安全物理路径
     */
    private Path resolveSafeUploadFile(String url) {
        String path = url;
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            if (path.startsWith(base)) {
                path = path.substring(base.length());
            }
        }
        int idx = path.indexOf("/uploads/");
        if (idx >= 0) {
            path = path.substring(idx);
        }
        if (!path.startsWith("/uploads/")) {
            throw new BusinessException("无效的文件地址");
        }
        String rel = path.substring("/uploads/".length());
        if (rel.contains("..") || rel.chars().filter(ch -> ch == '/').count() != 3) {
            throw new BusinessException("无效的文件路径");
        }
        String[] parts = rel.split("/");
        if (parts.length != 4) {
            throw new BusinessException("无效的文件路径");
        }
        if (!parts[0].matches("\\d{4}") || !parts[1].matches("\\d{2}") || !parts[2].matches("\\d{2}")) {
            throw new BusinessException("无效的文件路径");
        }
        String fileName = parts[3];
        if (!UPLOAD_FILE_NAME.matcher(fileName).matches()) {
            throw new BusinessException("无效的文件名");
        }
        String ext = getExtension(fileName);
        if (!isAllowedExt(ext)) {
            throw new BusinessException("不允许删除该类型文件");
        }
        Path base = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path target = base.resolve(parts[0]).resolve(parts[1]).resolve(parts[2]).resolve(fileName).normalize();
        if (!target.startsWith(base)) {
            throw new BusinessException("无效的文件路径");
        }
        return target;
    }
}
