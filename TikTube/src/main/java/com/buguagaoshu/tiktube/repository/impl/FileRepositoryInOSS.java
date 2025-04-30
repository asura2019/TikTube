package com.buguagaoshu.tiktube.repository.impl;


import com.buguagaoshu.tiktube.config.WebConfig;
import com.buguagaoshu.tiktube.config.WebConstant;
import com.buguagaoshu.tiktube.entity.OSSConfigEntity;
import com.buguagaoshu.tiktube.service.impl.OssConfigService;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-28
 */
@Repository
@Slf4j
public class FileRepositoryInOSS {
    private final OssConfigService ossConfigService;

    Map<Integer, MinioClient> minioClients = new HashMap<Integer, MinioClient>();

    Map<Integer, OSSConfigEntity> configEntityMap = new HashMap<>();

    @Autowired
    public FileRepositoryInOSS(OssConfigService ossConfigService) {
        this.ossConfigService = ossConfigService;
        // 初始化时更新客户端
        updateMinioClient();
    }

    /**
     * 当对象存储配置发生变化后，更新客户端
     * 更新 Minio 客户端
     * */
    public void updateMinioClient() {
        boolean flag = false;
        // 获取所有对象存储配置
        List<OSSConfigEntity> ossConfigEntities = this.ossConfigService.listAllOssConfigs();
        if (ossConfigEntities == null || ossConfigEntities.isEmpty()) {
            WebConfig.FILE_SAVE_LOCATION = 0;
            return;
        }
        // 创建客户端并保存
        minioClients.clear();
        for (OSSConfigEntity ossConfigEntity : ossConfigEntities) {
            configEntityMap.put(ossConfigEntity.getId(), ossConfigEntity);
            // 创建 Minio S3 兼容客户端
            try {
                if (ossConfigEntity.getStatus().equals(1)) {
                    WebConfig.FILE_SAVE_LOCATION = ossConfigEntity.getId();
                    flag = true;
                }
                MinioClient.Builder builder = MinioClient.builder()
                        .endpoint(ossConfigEntity.getEndpoint())
                        .credentials(ossConfigEntity.getAccessKey(), ossConfigEntity.getSecretKey());
                
                // 设置区域（如果有）
                if (ossConfigEntity.getRegion() != null && !ossConfigEntity.getRegion().isEmpty()) {
                    builder.region(ossConfigEntity.getRegion());
                }

                
                MinioClient minioClient = builder.build();

                //minioClient.traceOn(System.out);

                // 设置路径访问风格
                if (ossConfigEntity.getPathStyleAccess() != null) {
                    if (ossConfigEntity.getPathStyleAccess().equals(1)) {
                        minioClient.enableVirtualStyleEndpoint();
                    }
                }
                // 检查存储桶是否存在，不存在则创建
                boolean bucketExists = minioClient.bucketExists(
                        BucketExistsArgs.builder()
                                .bucket(ossConfigEntity.getBucketName())
                                .build()
                );
                
                if (!bucketExists && ossConfigEntity.getStatus() == 1) {
                    minioClient.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(ossConfigEntity.getBucketName())
                                    .build()
                    );
                    log.info("创建存储桶成功: {}", ossConfigEntity.getBucketName());
                }
                
                minioClients.put(ossConfigEntity.getId(), minioClient);
                log.info("初始化对象存储客户端成功: {}", ossConfigEntity.getConfigName());
            } catch (Exception e) {
                log.error("初始化对象存储客户端失败: {}, 错误信息: {}", ossConfigEntity.getConfigName(), e.getMessage(), e);
            }
        }
        if (!flag) {
            WebConfig.FILE_SAVE_LOCATION = 0;
        }
    }
    
    /**
     * 保存文件到对象存储
     * 
     * @param file 文件
     * @param objectName 对象名称，如果为空则自动生成
     * @param contentType 内容类型
     * @param ossConfigId 对象存储配置ID，如果为空则使用默认配置
     * @return 文件URL
     */
    public String saveFile(MultipartFile file, String objectName, String contentType, Integer ossConfigId) {
        try {

            OSSConfigEntity ossConfigEntity = configEntityMap.get(ossConfigId);
            // 获取MinioClient
            MinioClient minioClient = minioClients.get(ossConfigId);
            if (minioClient == null) {
                log.error("未找到对应的MinioClient WebConfig.FILE_SAVE_LOCATION = {}", ossConfigId);
                return null;
            }
            
            // 如果对象名为空，则自动生成
            if (objectName == null || objectName.isEmpty()) {
                objectName = UUID.randomUUID().toString().replaceAll("-", "") + 
                        getFileExtension(file.getOriginalFilename());
            }
            
            // 上传文件
            try (InputStream inputStream = file.getInputStream()) {
//                minioClient.uploadObject(
//                        UploadObjectArgs.builder()
//                                .bucket(ossConfigEntity.getBucketName())
//                                .object(objectName)
//                                .filename(localFilePath)
//                                .contentType(contentType)
//                                .build()
//                );
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(ossConfigEntity.getBucketName())
                                .object(objectName)
                                .contentType(contentType != null ? contentType : file.getContentType())
                                .stream(inputStream, file.getSize(), -1)
                                .build()
                );
                return objectName;
            } catch (Exception e) {
                log.error("文件上传失败: {}", e.getMessage());
                return null;
            }
        } catch (Exception e) {
            log.error("保存文件失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 删除文件
     * 
     * @param objectName 对象名称
     * @param ossConfigId 对象存储配置ID，如果为空则使用默认配置
     * @return 是否删除成功
     */
    public boolean deleteFile(String objectName, Integer ossConfigId) {
        try {
            // 获取对象存储配置
            OSSConfigEntity ossConfig = configEntityMap.get(ossConfigId);
            
            // 获取MinioClient
            MinioClient minioClient = minioClients.get(ossConfig.getId());
            if (minioClient == null) {
                log.error("未找到对应的MinioClient: {}", ossConfig.getConfigName());
                return false;
            }
            
            // 删除文件
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(ossConfig.getBucketName())
                            .object(objectName)
                            .build()
            );
            
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 获取文件URL
     * 
     * @param objectName 对象名称
     * @param ossConfigId 对象存储配置ID，如果为空则使用默认配置
     * @return 文件URL
     */
    public String getFileUrl(String objectName, Integer ossConfigId) {
        try {
            // 获取对象存储配置
            OSSConfigEntity ossConfig = configEntityMap.get(ossConfigId);
            
            // 获取MinioClient
            MinioClient minioClient = minioClients.get(ossConfig.getId());
            if (minioClient == null) {
                log.error("未找到对应的MinioClient: {}", ossConfig.getConfigName());
                return null;
            }
            
            // 如果配置了自定义域名，则使用自定义域名
            if (ossConfig.getUrlPrefix() != null && !ossConfig.getUrlPrefix().isEmpty()) {
                return ossConfig.getUrlPrefix() + "/" + objectName;
            }
            
            // 生成预签名URL

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(ossConfig.getBucketName())
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(WebConstant.OSS_DAY_TIME, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            log.error("获取文件URL失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 获取文件扩展名
     * 
     * @param filename 文件名
     * @return 文件扩展名（包含点号）
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 上传本地文件到对象存储
     * 
     * @param localFilePath 本地文件路径
     * @param objectName 对象名称，如果为空则自动生成
     * @param ossConfigId 对象存储配置ID，如果为空则使用默认配置
     * @return 文件URL
     */
    public String uploadLocalFile(String localFilePath, String objectName, Integer ossConfigId) {
        try {
            // 获取对象存储配置
            OSSConfigEntity ossConfigEntity = configEntityMap.get(ossConfigId);
            if (ossConfigEntity == null) {
                log.error("未找到对应的对象存储配置: {}", ossConfigId);
                return null;
            }
            
            // 获取MinioClient
            MinioClient minioClient = minioClients.get(ossConfigId);
            if (minioClient == null) {
                log.error("未找到对应的MinioClient: {}", ossConfigId);
                return null;
            }
            
            // 创建File对象
            java.io.File file = new java.io.File(localFilePath);
            if (!file.exists() || !file.isFile()) {
                log.error("本地文件不存在或不是文件: {}", localFilePath);
                return null;
            }
            
            // 如果对象名为空，则自动生成
            if (objectName == null || objectName.isEmpty()) {
                objectName = UUID.randomUUID().toString().replaceAll("-", "") + 
                        getFileExtension(file.getName());
            }
            
            // 获取文件内容类型
            String contentType = java.nio.file.Files.probeContentType(file.toPath());
            if (contentType == null) {
                // 如果无法确定内容类型，使用通用二进制类型
                contentType = "application/octet-stream";
            }
            
            // 上传文件
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(ossConfigEntity.getBucketName())
                            .object(objectName)
                            .filename(localFilePath)
                            .contentType(contentType)
                            .build()
            );
            
            return objectName;
        } catch (Exception e) {
            log.error("上传本地文件失败: {}, 错误信息: {}", localFilePath, e.getMessage(), e);
            return null;
        }
    }
}
