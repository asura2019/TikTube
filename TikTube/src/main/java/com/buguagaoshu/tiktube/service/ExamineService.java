package com.buguagaoshu.tiktube.service;

import com.buguagaoshu.tiktube.dto.ExamineDto;
import com.buguagaoshu.tiktube.entity.ArticleEntity;
import com.buguagaoshu.tiktube.entity.CommentEntity;
import com.buguagaoshu.tiktube.entity.DanmakuEntity;
import com.buguagaoshu.tiktube.enums.CommentType;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.enums.TypeCode;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-06
 */
@Service
@Slf4j
public class ExamineService {
    private final ArticleService articleService;

    private final CommentService commentService;

    private final DanmakuService danmakuService;

    @Autowired
    public ExamineService(ArticleService articleService,
                          CommentService commentService,
                          DanmakuService danmakuService) {
        this.articleService = articleService;
        this.commentService = commentService;
        this.danmakuService = danmakuService;
    }

    public ReturnCodeEnum examineArticle(ExamineDto examineDto, HttpServletRequest request) {
        return articleService.examine(examineDto, request);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnCodeEnum examineComment(CommentEntity comment, HttpServletRequest request) {
        CommentEntity entity = commentService.getById(comment.getId());
        if (entity == null) {
            return ReturnCodeEnum.NOO_FOUND;
        }
        if (entity.getStatus().equals(TypeCode.DELETE)) {
            return ReturnCodeEnum.NOO_FOUND;
        }
        if (!entity.getStatus().equals(TypeCode.NORMAL)) {
            // 审核通过
            if (comment.getStatus().equals(TypeCode.NORMAL)) {
                log.info("管理员 {} 审核通过了ID为: {} 的评论！", JwtUtil.getUserId(request), entity.getId());
                entity.setStatus(TypeCode.NORMAL);
                commentService.updateById(entity);
                // 审核通过后在发送消息
                ArticleEntity article = articleService.getById(entity.getArticleId());
                // fatherComment = this.getById(commentVo.getParentCommentId());
                CommentEntity fatherComment = null;
                if (entity.getType().equals(CommentType.SECOND_COMMENT)) {
                    fatherComment = commentService.getById(entity.getParentCommentId());
                }
                // 通知发送
                commentService.send(entity, fatherComment, article);
            }
        }
        return ReturnCodeEnum.SUCCESS;
    }

    public ReturnCodeEnum examineDanmaku(DanmakuEntity danmaku, HttpServletRequest request) {
        DanmakuEntity entity = danmakuService.getById(danmaku.getId());
        if (entity == null) {
            return ReturnCodeEnum.NOO_FOUND;
        }
        if (entity.getStatus().equals(TypeCode.DELETE)) {
            return ReturnCodeEnum.NOO_FOUND;
        }
        if (!entity.getStatus().equals(TypeCode.NORMAL)) {
            if (danmaku.getStatus().equals(TypeCode.NORMAL)) {
                log.info("管理员 {} 审核通过了ID为: {} 的弹幕！", JwtUtil.getUserId(request), entity.getId());
                entity.setStatus(TypeCode.NORMAL);
                danmakuService.updateById(entity);
            }
        }
        return ReturnCodeEnum.SUCCESS;
    }
}
