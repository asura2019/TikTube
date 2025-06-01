package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.config.WebConstant;
import com.buguagaoshu.tiktube.dto.TextArticleDto;
import com.buguagaoshu.tiktube.entity.ArticleTextEntity;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.repository.APICurrentLimitingRepository;
import com.buguagaoshu.tiktube.service.ArticleTextService;
import com.buguagaoshu.tiktube.utils.IpUtil;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-26
 */
@RestController
@RequestMapping("/api")
public class ArticleTextController {
    private final ArticleTextService articleTextService;

    private final APICurrentLimitingRepository currentLimitingRepository;

    private final IpUtil ipUtil;

    @Autowired
    public ArticleTextController(ArticleTextService articleTextService,
                                 APICurrentLimitingRepository currentLimitingRepository,
                                 IpUtil ipUtil) {
        this.articleTextService = articleTextService;
        this.currentLimitingRepository = currentLimitingRepository;
        this.ipUtil = ipUtil;
    }

    @GetMapping("/article/text/{id}")
    public ResponseDetails getText(@PathVariable(value = "id") Long id,
                                   HttpServletRequest request) {
        return ResponseDetails.ok().put("data", articleTextService.getText(id, request));
    }



    @PostMapping("/article/text/password")
    public ResponseDetails getPasswordText(@RequestBody ArticleTextEntity articleTextEntity,
                                           HttpServletRequest request) {
        String ip = ipUtil.getIpAddr(request);
        if (currentLimitingRepository.hasVisit(WebConstant.SIMPLE_CURRENT_LIMITING_TYPE_ARTICLE_PASSWORD, ip)) {
            ArticleTextEntity a = articleTextService.getPasswordArticleTextEntity(articleTextEntity.getId(), articleTextEntity.getPassword());
            if (a == null) {
                return ResponseDetails.ok(ReturnCodeEnum.NO_POWER);
            }
            return ResponseDetails.ok().put("data", a);
        }
        return ResponseDetails.ok(ReturnCodeEnum.COUNT_LIMIT);
    }


    @PostMapping("/article/text/update")
    public ResponseDetails updateText(@Valid @RequestBody TextArticleDto textArticleDto,
                                      HttpServletRequest request) {
        return ResponseDetails.ok(articleTextService.updateText(textArticleDto, request));
    }


    @PostMapping("/article/text")
    public ResponseDetails saveText(@Valid @RequestBody TextArticleDto textArticleDto,
                                    HttpServletRequest request) {
        return ResponseDetails.ok(articleTextService.saveText(textArticleDto, request));
    }
}
