package com.buguagaoshu.tiktube.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.tiktube.dao.OSSConfigDao;
import com.buguagaoshu.tiktube.entity.OSSConfigEntity;
import com.buguagaoshu.tiktube.entity.WebConfigEntity;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * OSS配置服务实现类
 * 
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-30
 */
@Service
@Slf4j
public class OssConfigService extends ServiceImpl<OSSConfigDao, OSSConfigEntity> {

    /**
     * 保存OSS配置
     *
     * @param ossConfig OSS配置信息
     * @return 保存结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOssConfig(OSSConfigEntity ossConfig) {
        // 设置创建时间
        if (ossConfig.getCreateTime() == null) {
            ossConfig.setCreateTime(System.currentTimeMillis());
        }
        
        // 设置更新时间
        ossConfig.setUpdateTime(System.currentTimeMillis());
        // 同时只能启用一个对象存储
        OSSConfigEntity defaultOssConfig = getDefaultOssConfig();
        if (defaultOssConfig != null && ossConfig.getStatus().equals(1)) {
            return false;
        }
        if (validateOssConfig(ossConfig)) {
            return this.save(ossConfig);
        } else {
            return false;
        }
    }

    /**
     * 更新OSS配置
     *
     * @param ossConfig OSS配置信息
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOssConfig(OSSConfigEntity ossConfig) {
        // 设置更新时间
        ossConfig.setUpdateTime(System.currentTimeMillis());
        // 同时只能启用一个对象存储
        OSSConfigEntity defaultOssConfig = getDefaultOssConfig();
        if (defaultOssConfig != null && !ossConfig.getId().equals(defaultOssConfig.getId())) {
            if (ossConfig.getStatus().equals(1)) {
                return false;
            }
        }

        if (validateOssConfig(ossConfig)) {
            return this.updateById(ossConfig);
        } else {
            return false;
        }
    }

    /**
     * 删除OSS配置
     *
     * @param id 配置ID
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOssConfig(Integer id) {
        OSSConfigEntity byId = this.getById(id);
        if (byId != null) {
            if (byId.getStatus().equals(1)) {
                return false;
            }
            return this.removeById(id);
        }
        return false;
    }

    /**
     * 根据ID获取OSS配置
     *
     * @param id 配置ID
     * @return OSS配置信息
     */
    public OSSConfigEntity getOssConfigById(Integer id) {
        return this.getById(id);
    }

    /**
     * 获取所有OSS配置
     *
     * @return OSS配置列表
     */
    public List<OSSConfigEntity> listAllOssConfigs() {
        QueryWrapper<OSSConfigEntity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("update_time");
        return this.list(wrapper);
    }


    /**
     * 获取默认启用的OSS配置
     * 如果有多个启用的配置，返回第一个
     *
     * @return 默认OSS配置
     */
    public OSSConfigEntity getDefaultOssConfig() {
        QueryWrapper<OSSConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.last("LIMIT 1");
        return this.getOne(queryWrapper);
    }

    /**
     * 验证OSS配置是否可用
     * 使用兼容S3的API进行验证
     *
     * @param ossConfig OSS配置信息
     * @return 验证结果
     */
    public boolean validateOssConfig(OSSConfigEntity ossConfig) {
        try {
            // 创建MinioClient对象
            MinioClient minioClient = buildMinioClient(ossConfig);
            
            // 检查存储桶是否存在
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(ossConfig.getBucketName())
                    .build()
            );
            
            return exists;
        } catch (Exception e) {
            log.error("验证OSS配置失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 构建MinioClient客户端
     *
     * @param ossConfig OSS配置信息
     * @return MinioClient实例
     */
    public MinioClient buildMinioClient(OSSConfigEntity ossConfig) {
        MinioClient.Builder builder = MinioClient.builder()
                .endpoint(ossConfig.getEndpoint())
                .credentials(ossConfig.getAccessKey(), ossConfig.getSecretKey());
        
        // 设置区域（如果有）
        if (ossConfig.getRegion() != null && !ossConfig.getRegion().isEmpty()) {
            builder.region(ossConfig.getRegion());
        }
        
        // 设置路径访问风格
        if (ossConfig.getPathStyleAccess() != null && ossConfig.getPathStyleAccess() == 0) {
            // Path style access
            builder.endpoint(ossConfig.getEndpoint());
        }
        
        return builder.build();
    }
}
