package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.cache.AdvertisementCache;
import com.buguagaoshu.tiktube.entity.AdvertisementEntity;
import com.buguagaoshu.tiktube.service.AdvertisementService;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-03
 */
@RestController
@RequestMapping("/api")
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }


    @GetMapping("/web/notice")
    public ResponseDetails nowAds(@RequestParam Integer type) {
        return ResponseDetails.ok().put("data", AdvertisementCache.advertisementCache.get(type));
    }

    @PostMapping("/admin/ads/save")
    public ResponseDetails save(@Valid @RequestBody AdvertisementEntity advertisement,
                                HttpServletRequest request) {
        return ResponseDetails.ok().put("data", advertisementService.save(advertisement, JwtUtil.getUserId(request)));
    }


    @PostMapping("/admin/ads/update")
    public ResponseDetails update(@Valid @RequestBody AdvertisementEntity advertisement,
                                  HttpServletRequest request) {
        return ResponseDetails.ok().put("data", advertisementService.update(advertisement, JwtUtil.getUserId(request)));
    }


    @GetMapping("/admin/ads/list")
    public ResponseDetails get(@RequestParam Map<String, Object> params) {
        return ResponseDetails.ok().put("data", advertisementService.queryPage(params));
    }
}
