package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.pipe.AiExaminePipe;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-17
 * AI审核管道控制器
 */
//@RestController
//@RequestMapping("/api/admin")
public class AiExaminePipeController {

    private final AiExaminePipe aiExaminePipe;

    //@Autowired
    public AiExaminePipeController(AiExaminePipe aiExaminePipe) {
        this.aiExaminePipe = aiExaminePipe;
    }

    /**
     * 获取审核队列状态
     */
    //@GetMapping("/ai/examine/queue/status")
    public ResponseDetails getQueueStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("commentQueueSize", aiExaminePipe.getCommentQueueSize());
        status.put("danmakuQueueSize", aiExaminePipe.getDanmakuQueueSize());
        return ResponseDetails.ok().put("data", status);
    }
}