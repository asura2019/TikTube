package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.cache.WebSettingCache;
import com.buguagaoshu.tiktube.entity.WebSettingEntity;
import com.buguagaoshu.tiktube.service.WebSettingService;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
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

    @Autowired
    public WebSettingController(WebSettingCache webSettingCache, WebSettingService webSettingService) {
        this.webSettingCache = webSettingCache;
        this.webSettingService = webSettingService;
    }


    /**
     * 返回首页公告内容
     * */
    @GetMapping("/api/web/notice")
    public ResponseDetails notice() {
        // TODO 暂时固定，后期修改为动态的
        Map<String, String> map = new HashMap<>();
        map.put("title", "系统消息");
        map.put("content", "该版本仅为展示系统，故管理员关闭了普通用户的投稿，评论功能，如需要体验这两功能，请自行部署体验！");
        return ResponseDetails.ok().put("data", map);
    }

    @GetMapping("/api/web/info")
    public ResponseDetails webInfo() {
        return ResponseDetails.ok().put("data", webSettingCache.getWebSettingEntity());
    }


    @PostMapping("/api/admin/setting/save")
    public ResponseDetails updateWebSetting(@RequestBody WebSettingEntity WebSettingEntity) {
        return ResponseDetails.ok(webSettingService.saveSetting(WebSettingEntity));
    }
}
