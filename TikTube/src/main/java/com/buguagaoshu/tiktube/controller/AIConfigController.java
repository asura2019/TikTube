package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.config.AIPromptConfig;
import com.buguagaoshu.tiktube.entity.AiConfigEntity;
import com.buguagaoshu.tiktube.enums.TypeCode;
import com.buguagaoshu.tiktube.service.AIConfigServer;
import com.buguagaoshu.tiktube.utils.IpUtil;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-17
 */
@RestController
@RequestMapping("/api")
public class AIConfigController {
    public final AIConfigServer aiConfigServer;

    private final IpUtil ipUtil;

    @Autowired
    public AIConfigController(AIConfigServer aiConfigServer, IpUtil ipUtil) {
        this.aiConfigServer = aiConfigServer;
        this.ipUtil = ipUtil;
    }

    @PostMapping("/admin/ai/close")
    public ResponseDetails closeAIConfig(HttpServletRequest request) {
        return ResponseDetails.ok(aiConfigServer.clousAiExamine(JwtUtil.getUserId(request), ipUtil.getIpAddr(request)));
    }


    @PostMapping("/admin/ai/open")
    public ResponseDetails openAIConfig(HttpServletRequest request) {
        return ResponseDetails.ok(aiConfigServer.openAIConfig(JwtUtil.getUserId(request), ipUtil.getIpAddr(request)));
    }

    @PostMapping("/admin/ai/prompt")
    public ResponseDetails getPrompt() {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "");
        map.put(TypeCode.AI_TYPE_EXAMINE_COMMENT, AIPromptConfig.EXAMINE_COMMENT_AND_DANMAKU);
        map.put(TypeCode.AI_TYPE_TEXT_ABSTRACT, AIPromptConfig.TEXT_ABSTRACT);
        return ResponseDetails.ok().put("data", map);
    }


    @PostMapping("/admin/ai/save")
    public ResponseDetails save(@RequestBody AiConfigEntity aiConfigEntity,
                                HttpServletRequest request) {
        return ResponseDetails.ok().put("data",
                aiConfigServer.saveAIConfig(aiConfigEntity, JwtUtil.getUserId(request)));
    }

    /**
     * 获取所有AI配置列表
     */
    @GetMapping("/admin/ai/list")
    public ResponseDetails list() {
        return ResponseDetails.ok().put("data", aiConfigServer.queryPage());
    }


    /**
     * 更新AI配置
     */
    @PostMapping("/admin/ai/update")
    public ResponseDetails update(@RequestBody AiConfigEntity aiConfigEntity,
                                  HttpServletRequest request) {
        return ResponseDetails.ok().put("data",
                aiConfigServer.updateAiConfig(aiConfigEntity, JwtUtil.getUserId(request)));
    }

    /**
     * 删除AI配置
     */
    @PostMapping("/admin/ai/delete")
    public ResponseDetails delete(@RequestBody AiConfigEntity aiConfigEntity) {
        return ResponseDetails.ok().put("data", aiConfigServer.deleteAI(aiConfigEntity));
    }
}
