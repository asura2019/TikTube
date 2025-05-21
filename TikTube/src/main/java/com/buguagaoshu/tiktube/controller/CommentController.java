package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.config.WebConstant;
import com.buguagaoshu.tiktube.entity.CommentEntity;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.repository.APICurrentLimitingRepository;
import com.buguagaoshu.tiktube.service.CommentService;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.vo.CommentVo;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2022-08-25 15:57
 */
@RestController
public class CommentController {
    private final CommentService commentService;

    private final APICurrentLimitingRepository apicurrentLimitingRepository;

    @Autowired
    public CommentController(CommentService commentService,
                             APICurrentLimitingRepository apicurrentLimitingRepository) {
        this.commentService = commentService;
        this.apicurrentLimitingRepository = apicurrentLimitingRepository;
    }

    @PostMapping("/api/comment/save")
    public ResponseDetails saveComment(@Valid @RequestBody CommentVo commentVo,
                                       HttpServletRequest request) {
        if (apicurrentLimitingRepository.hasVisit(WebConstant.SIMPLE_CURRENT_LIMITING_TYPE_COMMENT, JwtUtil.getUserId(request))) {
            CommentEntity comment = commentService.saveComment(commentVo, request);
            if (comment == null) {
                return ResponseDetails.ok(1006, "所评论的帖子可能被删除被锁定或没有评论权限！");
            }
            return ResponseDetails.ok().put("data", comment);
        }
        return ResponseDetails.ok(ReturnCodeEnum.COUNT_LIMIT);
    }


    @GetMapping("/api/comment/list")
    public ResponseDetails commentList(@RequestParam Map<String, Object> params,
                                       HttpServletRequest request) {
        return ResponseDetails.ok().put("data", commentService.queryPage(params, request));
    }


    @GetMapping("/api/admin/comment/list")
    public ResponseDetails getAllComment(@RequestParam Map<String, Object> params) {
        return ResponseDetails.ok().put("data", commentService.getAllComment(params));
    }


    @PostMapping("/api/admin/comment/toggle")
    public ResponseDetails toggleComment(@RequestBody CommentEntity commentEntity) {
        return ResponseDetails.ok().put("data", commentService.toggleCommentStatus(commentEntity.getId()));
    }
}
