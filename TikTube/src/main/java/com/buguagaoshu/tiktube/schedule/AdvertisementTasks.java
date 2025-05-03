package com.buguagaoshu.tiktube.schedule;

import com.buguagaoshu.tiktube.service.AdvertisementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-03
 */
@Component
@Slf4j
public class AdvertisementTasks {
    private final AdvertisementService advertisementService;

    @Autowired
    public AdvertisementTasks(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @Scheduled(fixedRate = 3600000)
    public void updateAds() {
        log.info("更新广告缓存");
        advertisementService.getNowAdvertisement();
        log.info("广告缓存更新完成");
    }
}
