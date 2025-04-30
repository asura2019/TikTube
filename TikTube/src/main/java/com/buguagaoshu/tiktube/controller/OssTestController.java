package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.repository.impl.FileRepositoryInOSSTest;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-28
 * 对象存储测试类
 */
//@RestController
//@RequestMapping("/api/oss/test")
public class OssTestController {

    private final FileRepositoryInOSSTest fileRepositoryInOSSTest;

    //@Autowired
    public OssTestController(FileRepositoryInOSSTest fileRepositoryInOSSTest) {
        this.fileRepositoryInOSSTest = fileRepositoryInOSSTest;
    }

    /**
     * 文件上传测试
     *
     * @param file 上传的文件
     * @param bucketName 桶名称（可选，默认为test）
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseDetails uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bucketName", defaultValue = "test") String bucketName) {
        try {
            // 生成唯一的对象名称
            String originalFilename = file.getOriginalFilename();
            String objectName = UUID.randomUUID().toString();
            if (originalFilename != null && !originalFilename.isEmpty()) {
                // 保留原始文件扩展名
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                objectName = objectName + extension;
            }

            // 获取文件的内容类型
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 上传文件
            String fileUrl = fileRepositoryInOSSTest.uploadFile(bucketName, objectName, file, contentType);
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("bucketName", bucketName);
            result.put("objectName", objectName);
            result.put("contentType", contentType);
            result.put("size", file.getSize());
            
            return ResponseDetails.ok().put("data", result);
        } catch (Exception e) {
            return ResponseDetails.ok(500, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件URL测试
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param duration 过期时间（可选，默认为7）
     * @param timeUnit 时间单位（可选，默认为天）
     * @return 文件URL
     */
    @GetMapping("/url")
    public ResponseDetails getFileUrl(
            @RequestParam("bucketName") String bucketName,
            @RequestParam("objectName") String objectName,
            @RequestParam(value = "duration", defaultValue = "7") Integer duration,
            @RequestParam(value = "timeUnit", defaultValue = "DAYS") String timeUnit) {
        try {
            TimeUnit unit = TimeUnit.valueOf(timeUnit);
            String fileUrl = fileRepositoryInOSSTest.getFileUrl(bucketName, objectName, duration, unit);
            
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("bucketName", bucketName);
            result.put("objectName", objectName);
            result.put("duration", duration);
            result.put("timeUnit", timeUnit);
            
            // 获取对象存储路径
            String storagePath = fileRepositoryInOSSTest.getObjectStoragePath(bucketName, objectName);
            result.put("storagePath", storagePath);
            
            return ResponseDetails.ok().put("data", result);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                 XmlParserException | InternalException e) {
            return ResponseDetails.ok(500, "获取文件URL失败：" + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseDetails.ok(400, "无效的时间单位：" + timeUnit);
        }
    }

    /**
     * 删除文件测试
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public ResponseDetails deleteFile(
            @RequestParam("bucketName") String bucketName,
            @RequestParam("objectName") String objectName) {
        try {
            fileRepositoryInOSSTest.deleteFile(bucketName, objectName);
            
            Map<String, Object> result = new HashMap<>();
            result.put("bucketName", bucketName);
            result.put("objectName", objectName);
            result.put("message", "文件删除成功");
            
            return ResponseDetails.ok().put("data", result);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                 XmlParserException | InternalException e) {
            return ResponseDetails.ok(500, "文件删除失败：" + e.getMessage());
        }
    }
}
