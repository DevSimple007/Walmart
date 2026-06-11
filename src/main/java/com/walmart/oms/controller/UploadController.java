package com.walmart.oms.controller;

import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传", description = "文件上传相关接口")
public class UploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("")
    @Operation(summary = "上传文件", description = "上传图片文件")
    public R<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "productName", required = false) String productName) {
        
        if (file.isEmpty()) {
            return R.error(400, "文件不能为空");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unknown";
        }

        // 检查文件类型
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!suffix.matches("\\.(jpg|jpeg|png|gif|webp)$")) {
            return R.error(400, "只支持jpg/png/gif/webp格式的图片");
        }

        // 检查文件大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return R.error(400, "文件大小不能超过5MB");
        }

        // 构建存储路径：商品图片/分类名/商品名/
        String subPath = "商品图片";
        if (category != null && !category.isEmpty()) {
            subPath += "/" + sanitizePath(category);
        }
        if (productName != null && !productName.isEmpty()) {
            subPath += "/" + sanitizePath(productName);
        }

        String dirPath = uploadPath + File.separator + subPath;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成新文件名
        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;
        String filePath = dirPath + File.separator + newFilename;

        // 保存文件
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            return R.error(500, "文件保存失败");
        }

        // 返回访问路径（带context-path前缀）
        String url = "/api/uploads/" + subPath.replace("\\", "/") + "/" + newFilename;
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("filename", newFilename);
        return R.success(result);
    }

    /**
     * 清理路径中的特殊字符，防止路径穿越攻击
     */
    private String sanitizePath(String path) {
        if (path == null) return "";
        // 只保留中文、英文、数字、空格、下划线、横线
        return path.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9_\\-\\s]", "")
                   .replaceAll("\\.\\.", "")
                   .trim();
    }
}
