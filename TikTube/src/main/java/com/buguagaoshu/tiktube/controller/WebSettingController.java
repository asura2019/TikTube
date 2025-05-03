package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.TikTubeApplication;
import com.buguagaoshu.tiktube.cache.WebSettingCache;
import com.buguagaoshu.tiktube.config.WebConfig;
import com.buguagaoshu.tiktube.entity.OSSConfigEntity;
import com.buguagaoshu.tiktube.entity.WebSettingEntity;
import com.buguagaoshu.tiktube.repository.impl.FileRepositoryInOSS;
import com.buguagaoshu.tiktube.service.WebSettingService;
import com.buguagaoshu.tiktube.service.impl.OssConfigService;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-05 20:14
 */
@RestController
public class WebSettingController {
    private final WebSettingCache webSettingCache;

    private final WebSettingService webSettingService;

    private final OssConfigService ossConfigService;

    private final FileRepositoryInOSS repositoryInOSS;

    @Autowired
    public WebSettingController(WebSettingCache webSettingCache,
                                WebSettingService webSettingService,
                                OssConfigService ossConfigService,
                                FileRepositoryInOSS repositoryInOSS) {
        this.webSettingCache = webSettingCache;
        this.webSettingService = webSettingService;
        this.ossConfigService = ossConfigService;
        this.repositoryInOSS = repositoryInOSS;
    }


    @GetMapping("/api/version")
    public ResponseDetails webVersion() {
        return ResponseDetails.ok().put("version", TikTubeApplication.VERSION);
    }

    @GetMapping("/api/web/info")
    public ResponseDetails webInfo() {
        return ResponseDetails.ok().put("data", webSettingCache.getWebSettingEntity());
    }


    @PostMapping("/api/admin/setting/save")
    public ResponseDetails updateWebSetting(@RequestBody WebSettingEntity WebSettingEntity) {
        return ResponseDetails.ok(webSettingService.saveSetting(WebSettingEntity));
    }

    
    /**
     * 获取所有OSS配置列表
     */
    @GetMapping("/api/admin/oss/list")
    public ResponseDetails listOssConfigs() {
        return ResponseDetails.ok().put("data", ossConfigService.listAllOssConfigs());
    }

    /**
     * 获取单个OSS配置信息
     */
    @GetMapping("/api/admin/oss/{id}")
    public ResponseDetails getOssConfig(@PathVariable("id") Integer id) {
        return ResponseDetails.ok().put("data", ossConfigService.getOssConfigById(id));
    }

    /**
     * 保存OSS配置
     */
    @PostMapping("/api/admin/oss/save")
    public ResponseDetails saveOssConfig(@RequestBody OSSConfigEntity ossConfig,
                                         HttpServletRequest request) {
        // 设置创建者ID
        ossConfig.setCreatorId(JwtUtil.getUserId(request));
        // 设置更新者ID
        ossConfig.setUpdaterId(JwtUtil.getUserId(request));
        boolean b = ossConfigService.saveOssConfig(ossConfig);
        if (b) {
            repositoryInOSS.updateMinioClient();
        }
        return ResponseDetails.ok().put("data", b);
    }

    /**
     * 更新OSS配置
     */
    @PostMapping("/api/admin/oss/update")
    public ResponseDetails updateOssConfig(@RequestBody OSSConfigEntity ossConfig,
                                           HttpServletRequest request) {
        // 设置更新者ID
        ossConfig.setUpdaterId(JwtUtil.getUserId(request));
        boolean b = ossConfigService.updateOssConfig(ossConfig);
        if (b) {
            repositoryInOSS.updateMinioClient();
        }
        return ResponseDetails.ok().put("data", b);
    }

    @GetMapping("/api/admin/oss/location")
    public ResponseDetails getSaveLocation() {
        return ResponseDetails.ok().put("data", WebConfig.FILE_SAVE_LOCATION);
    }

    /**
     * 删除OSS配置
     */
    @PostMapping("/api/admin/oss/delete/{id}")
    public ResponseDetails deleteOssConfig(@PathVariable("id") Integer id) {
        boolean b = ossConfigService.deleteOssConfig(id);
        if (b) {
            repositoryInOSS.updateMinioClient();
        }
        return ResponseDetails.ok().put("data", b);
    }

    /**
     * 验证OSS配置是否可用
     */
    @PostMapping("/api/admin/oss/validate")
    public ResponseDetails validateOssConfig(@RequestBody OSSConfigEntity ossConfig) {
        return ResponseDetails.ok().put("data", ossConfigService.validateOssConfig(ossConfig));
    }
}
