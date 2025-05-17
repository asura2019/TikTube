package com.buguagaoshu.tiktube.model;

import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-17
 */
@Data
public class AiExamineCommentAndDanmakuResult {
    private Boolean result = false;
    private String message = "";
    private Long token = 0L;
    private Long aiConfigId;
}
