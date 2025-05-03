package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.cache.AdsCountRecorder;
import com.buguagaoshu.tiktube.cache.AdvertisementCache;
import com.buguagaoshu.tiktube.entity.AdvertisementEntity;
import com.buguagaoshu.tiktube.service.AdvertisementService;
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
 * @create 2025-05-03
 */
@RestController
@RequestMapping("/api")
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    private final AdsCountRecorder adsCountRecorder;

    private final IpUtil ipUtil;

    @Autowired
    public AdvertisementController(AdvertisementService advertisementService,
                                   AdsCountRecorder adsCountRecorder,
                                   IpUtil ipUtil) {
        this.advertisementService = advertisementService;
        this.adsCountRecorder = adsCountRecorder;
        this.ipUtil = ipUtil;
    }


    @GetMapping("/web/notice")
    public ResponseDetails nowAds(@RequestParam Integer type) {
        return ResponseDetails.ok().put("data", AdvertisementCache.advertisementCache.get(type));
    }

    @PostMapping("/web/notice/click/{adId}")
    public ResponseDetails adsCount(@PathVariable Integer adId, HttpServletRequest request) {
        adsCountRecorder.recordAds(adId, ipUtil.getIpAddr(request));
        return ResponseDetails.ok();
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
