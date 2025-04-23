package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.cache.PlayCountRecorder;
import com.buguagaoshu.tiktube.entity.PlayRecordingEntity;
import com.buguagaoshu.tiktube.service.PlayRecordingWithArticleService;
import com.buguagaoshu.tiktube.utils.IpUtil;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-09 18:13
 */
@RestController
public class PlayRecordingController {
    private final PlayRecordingWithArticleService playRecordingWithArticleService;

    private final PlayCountRecorder playCountRecorder;

    private final IpUtil ipUtil;

    @Autowired
    public PlayRecordingController(PlayRecordingWithArticleService playRecordingWithArticleService,
                                   PlayCountRecorder playCountRecorder, IpUtil ipUtil) {
        this.playRecordingWithArticleService = playRecordingWithArticleService;
        this.playCountRecorder = playCountRecorder;
        this.ipUtil = ipUtil;
    }

    @GetMapping("/api/user/playrecording/list")
    public ResponseDetails list(@RequestParam Map<String, Object> params,
                                HttpServletRequest request) {
        return ResponseDetails.ok().put("data", playRecordingWithArticleService.playRecordingList(params, request));
    }


    @PostMapping("/api/user/playrecording/save")
    public ResponseDetails save(@RequestBody PlayRecordingEntity playRecording, HttpServletRequest request) {

        return ResponseDetails.ok().put("data",
                playRecordingWithArticleService.savePlayLog(playRecording, request));
    }

    /**
     * 增加播放量
     * */
    @PostMapping("/api/article/playrecording/view/{id}")
    public ResponseDetails addViewCount(@PathVariable("id") Long id, HttpServletRequest request) {
        playCountRecorder.recordPlay(id, ipUtil.getIpAddr(request));
        return ResponseDetails.ok();
    }
}
