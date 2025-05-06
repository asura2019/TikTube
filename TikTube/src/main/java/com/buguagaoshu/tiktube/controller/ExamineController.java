package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.dto.ExamineDto;
import com.buguagaoshu.tiktube.entity.CommentEntity;
import com.buguagaoshu.tiktube.entity.DanmakuEntity;
import com.buguagaoshu.tiktube.enums.ExamineTypeEnum;
import com.buguagaoshu.tiktube.service.ArticleService;
import com.buguagaoshu.tiktube.service.ExamineService;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-08 19:06
 */
@RestController
public class ExamineController {

    private final ExamineService examine;
    private final ExamineService examineService;

    @Autowired
    public ExamineController(ExamineService examine, ExamineService examineService) {
        this.examine = examine;
        this.examineService = examineService;
    }


    @GetMapping("/api/examine/item")
    public ResponseDetails examineTypeItem() {

        List<Map<String, Object>> item = new ArrayList<>();
        for (ExamineTypeEnum e : ExamineTypeEnum.values()) {
            if (e.getCode() == ExamineTypeEnum.PENDING_REVIEW.getCode() || e.getCode() == ExamineTypeEnum.SUCCESS.getCode()) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("code", e.getCode());
            map.put("message", e.getMsg());
            item.add(map);
        }
        return ResponseDetails.ok().put("data", item);
    }


    @PostMapping("/api/admin/examine")
    public ResponseDetails examine(@Valid @RequestBody ExamineDto examineDto,
                                   HttpServletRequest request) {
        return ResponseDetails.ok(examineService.examineArticle(examineDto, request));
    }


    @PostMapping("/api/admin/examine/comment")
    public ResponseDetails examineComment(@RequestBody CommentEntity comment,
                                          HttpServletRequest request) {
        return ResponseDetails.ok(examineService.examineComment(comment, request));
    }


    @PostMapping("/api/admin/examine/danmaku")
    public ResponseDetails examineDanmaku(@RequestBody DanmakuEntity danmaku,
                                          HttpServletRequest request) {
        return ResponseDetails.ok(examineService.examineDanmaku(danmaku, request));
    }
}
