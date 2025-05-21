package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.config.WebConstant;
import com.buguagaoshu.tiktube.dto.ArtDanmakuDto;
import com.buguagaoshu.tiktube.entity.DanmakuEntity;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.repository.APICurrentLimitingRepository;
import com.buguagaoshu.tiktube.service.DanmakuService;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-07 16:02
 * TODO 使用消息队列，拆分弹幕服务，达到读写分离
 */
@RestController
public class DanmakuController {

    private final DanmakuService danmakuService;

    private final APICurrentLimitingRepository currentLimitingRepository;

    @Autowired
    public DanmakuController(DanmakuService danmakuService,
                             APICurrentLimitingRepository currentLimitingRepository) {
        this.danmakuService = danmakuService;
        this.currentLimitingRepository = currentLimitingRepository;
    }


    @GetMapping("/api/danmaku/v1")
    public ResponseDetails getArt(@RequestParam("id") Long id) {
        return ResponseDetails.ok().put("data", danmakuService.artDanmakuList(id, 1000));
    }

    @PostMapping("/api/danmaku/v1")
    public ResponseDetails saveArt(@Valid @RequestBody ArtDanmakuDto danmakuDto,
                                   HttpServletRequest request) {
        if (currentLimitingRepository.hasVisit(WebConstant.SIMPLE_CURRENT_LIMITING_TYPE_DANMAKU, JwtUtil.getUserId(request))) {
            ReturnCodeEnum codeEnum = danmakuService.saveArtDanmaku(danmakuDto, request);
            if (codeEnum.equals(ReturnCodeEnum.NO_LOGIN)) {
                return ResponseDetails.ok(codeEnum).put("code", 1);
            }
            if (codeEnum.equals(ReturnCodeEnum.SUCCESS)) {
                return ResponseDetails.ok(codeEnum).put("code", 0);
            }
            return ResponseDetails.ok(codeEnum).put("code", -1);
        }
        return ResponseDetails.ok(ReturnCodeEnum.COUNT_LIMIT).put("code", 1);
    }

    /**
     * 获取所有弹幕列表（管理员接口）
     */
    @GetMapping("/api/admin/danmaku/list")
    public ResponseDetails getAllDanmaku(@RequestParam Map<String, Object> params) {
        return ResponseDetails.ok().put("data", danmakuService.getAllDanmaku(params));
    }


    /**
     * 切换弹幕状态（管理员接口）
     */
    @PostMapping("/api/admin/danmaku/toggle")
    public ResponseDetails toggleDanmakuStatus(@RequestBody DanmakuEntity danmakuEntity) {
        return ResponseDetails.ok().put("data", danmakuService.toggleDanmakuStatus(danmakuEntity.getId()));
    }

}
