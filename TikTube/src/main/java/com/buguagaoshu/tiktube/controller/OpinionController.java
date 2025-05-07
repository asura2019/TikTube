package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.entity.OpinionEntity;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.service.OpinionService;
import com.buguagaoshu.tiktube.utils.IpUtil;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-07
 */
@RestController
@RequestMapping("/api")
public class OpinionController {

    private final OpinionService opinionService;

    private final IpUtil ipUtil;

    @Autowired
    public OpinionController(OpinionService opinionService,
                             IpUtil ipUtil) {
        this.opinionService = opinionService;
        this.ipUtil = ipUtil;
    }

    @GetMapping("/admin/opinion/list")
    public ResponseDetails list(@RequestParam Map<String, Object> params) {
        return ResponseDetails.ok().put("data", opinionService.queryPage(params));
    }

    @PostMapping("/admin/opinion/acceptance")
    public ResponseDetails acceptance(@RequestBody OpinionEntity opinionEntity,
                             HttpServletRequest request) {
        return ResponseDetails.ok(opinionService.acceptance(opinionEntity, JwtUtil.getUserId(request)));
    }

    @PostMapping("/opinion/save")
    public ResponseDetails save(@Valid @RequestBody OpinionEntity opinionEntity,
                                HttpServletRequest request) {
        opinionEntity.setIp(ipUtil.getIpAddr(request));
        opinionEntity.setCity(ipUtil.getCity(opinionEntity.getIp()));
        opinionEntity.setUa(ipUtil.getUa(request));
        return ResponseDetails.ok(opinionService.save(opinionEntity, JwtUtil.getUserId(request)));
    }


    @GetMapping("/opinion/info")
    public ResponseDetails getInfo(@RequestParam Long id, HttpServletRequest request) {
        OpinionEntity info = opinionService.info(id, JwtUtil.getUserId(request));
        if (info != null) {
            return ResponseDetails.ok().put("data", info);
        }
        return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
    }
}
