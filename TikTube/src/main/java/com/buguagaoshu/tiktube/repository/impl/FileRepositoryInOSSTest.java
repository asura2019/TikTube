package com.buguagaoshu.tiktube.repository.impl;

import com.buguagaoshu.tiktube.config.MyConfigProperties;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-28
 */
@Repository
public class FileRepositoryInOSSTest {

    private final MinioClient minioClient;
    private final MyConfigProperties myConfigProperties;

    public FileRepositoryInOSSTest(MyConfigProperties myConfigProperties) {
        this.myConfigProperties = myConfigProperties;

        this.minioClient = MinioClient.builder()
                .endpoint(
                        myConfigProperties.getOssEndpoint()
                )
                .credentials(
                        myConfigProperties.getOssAccessKey(),
                        myConfigProperties.getOssSecretKey()
                )
                .build();
    }

    /**
     * 流式上传文件到对象存储
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param file 文件
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    public String uploadFile(String bucketName, String objectName, MultipartFile file, String contentType) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        try {
            // 检查桶是否存在，不存在则创建
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // 上传文件
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );
            inputStream.close();

            // 返回文件URL
            return getFileUrl(bucketName, objectName);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取文件访问URL
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @return 文件访问URL
     */
    public String getFileUrl(String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 默认过期时间为7天
        return getFileUrl(bucketName, objectName, 7, TimeUnit.DAYS);
    }

    /**
     * 获取文件访问URL，可指定过期时间
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @param duration 过期时间数值
     * @param unit 过期时间单位
     * @return 文件访问URL
     */
    public String getFileUrl(String bucketName, String objectName, int duration, TimeUnit unit) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(duration, unit)
                        .build()
        );
    }

    /**
     * 获取对象存储地址
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @return 对象存储地址
     */
    public String getObjectStoragePath(String bucketName, String objectName) {
        return myConfigProperties.getOssEndpoint() + "/" + bucketName + "/" + objectName;
    }

    /**
     * 删除文件
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     */
    public void deleteFile(String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }
}
