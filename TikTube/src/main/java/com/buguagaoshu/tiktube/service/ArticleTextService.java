package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.tiktube.cache.CategoryCache;
import com.buguagaoshu.tiktube.cache.CountRecorder;
import com.buguagaoshu.tiktube.cache.PlayCountRecorder;
import com.buguagaoshu.tiktube.cache.WebSettingCache;
import com.buguagaoshu.tiktube.dao.ArticleTextDao;
import com.buguagaoshu.tiktube.dto.TextArticleDto;
import com.buguagaoshu.tiktube.entity.*;
import com.buguagaoshu.tiktube.enums.*;
import com.buguagaoshu.tiktube.exception.UserNotLoginException;
import com.buguagaoshu.tiktube.utils.IpUtil;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.utils.MarkdownUtils;
import com.buguagaoshu.tiktube.vo.ArticleViewData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-26
 */
@Slf4j
@Service
public class ArticleTextService extends ServiceImpl<ArticleTextDao, ArticleTextEntity> {
    private final ArticleService articleService;
    private final CategoryCache categoryCache;
    private final WebSettingCache webSettingCache;
    private final FileTableService fileTableService;
    private final UserService userService;
    private final IpUtil ipUtil;
    private final CountRecorder countRecorder;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final PlayCountRecorder playCountRecorder;

    @Autowired
    public ArticleTextService(ArticleService articleService,
                              CategoryCache categoryCache,
                              WebSettingCache webSettingCache,
                              FileTableService fileTableService,
                              UserService userService,
                              IpUtil ipUtil, CountRecorder countRecorder,
                              PlayCountRecorder playCountRecorder) {
        this.articleService = articleService;
        this.categoryCache = categoryCache;
        this.webSettingCache = webSettingCache;
        this.fileTableService = fileTableService;
        this.userService = userService;
        this.ipUtil = ipUtil;
        this.countRecorder = countRecorder;
        this.playCountRecorder = playCountRecorder;
    }


    public ArticleViewData getText(long id, HttpServletRequest request) {
        // 获取用户ID，未登录用户为-1
        long userId = -1;
        try {
            userId = JwtUtil.getUserId(request);
        } catch (UserNotLoginException ignored) {}

        // 构建查询条件
        QueryWrapper<ArticleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        wrapper.eq("status", ArticleStatusEnum.NORMAL.getCode());
        wrapper.eq("type", FileTypeEnum.ARTICLE.getCode());

        // 判断是否为管理员
        boolean isAdmin = false;
        try {
            if (RoleTypeEnum.ADMIN.getRole().equals(JwtUtil.getRole(request))) {
                isAdmin = true;
            }
        } catch (Exception e) {
            // log.error("获取用户角色失败: {}", e.getMessage());
        }

        ArticleEntity articleEntity = articleService.getOne(wrapper);
        if (articleEntity == null) {
            return createHiddenArticleView();
        }

        // 如果不是管理员，且视频未通过审核
        if (!isAdmin && articleEntity.getExamineStatus() != ExamineTypeEnum.SUCCESS.getCode() && !articleEntity.getUserId().equals(articleEntity.getUserId())) {
            return createHiddenArticleView();
        }

        return buildArticleViewData(articleEntity, userId);
    }

    private ArticleViewData buildArticleViewData(ArticleEntity article, long userId) {
        ArticleViewData viewData = new ArticleViewData();
        countRecorder.syncArticleCount(article);

        // 数据同步
        BeanUtils.copyProperties(article, viewData);
        // 添加作者信息
        UserEntity author = userService.getById(article.getUserId());
        if (author != null) {
            viewData.setUsername(author.getUsername());
            viewData.setFollowCount(author.getFollowCount());
            viewData.setFansCount(author.getFansCount());
            viewData.setAvatarUrl(author.getAvatarUrl());
            viewData.setIntroduction(author.getIntroduction());
        }
        viewData.setDanmakuCount(article.getDanmakuCount());
        // 获取文章内容
        List<ArticleTextEntity> articleTextByArticleId = findArticleTextByArticleId(article.getId());
        for (ArticleTextEntity articleTextEntity : articleTextByArticleId) {
            if(articleTextEntity.getType() != TypeCode.ARTICLE_TEXT_NORMAL) {
                articleTextEntity.setContent("");
                articleTextEntity.setPassword("");
            }
        }

        viewData.setText(articleTextByArticleId);
        // 添加标签
        try {
            if (StringUtils.hasText(article.getTag())) {
                viewData.setTag((List<String>) OBJECT_MAPPER.readValue(article.getTag(), List.class));
            }
        } catch (JsonProcessingException e) {
            log.warn("视频id为 {} 的投稿反序列化标签时出现异常", article.getId());
        }

        // 添加分类信息
        addCategoryInfo(viewData, article.getCategory());

        viewData.setIsShow(true);
        // 增加播放量
        viewData.setViewCount(viewData.getViewCount() + playCountRecorder.getPlayCount(article.getId()));
        return viewData;
    }

    public List<ArticleTextEntity> findArticleTextByArticleId(long id) {
        QueryWrapper<ArticleTextEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("article_id", id);
        return this.list(wrapper);
    }

    private void addCategoryInfo(ArticleViewData viewData, Integer categoryId) {
        // 由于addUserInfo方法已集成类别信息的添加，此方法仅用于其他地方的单独调用
        Map<Integer, CategoryEntity> categoryMap = categoryCache.getCategoryEntityMap();
        CategoryEntity categoryEntity = categoryMap.get(categoryId);
        if (categoryEntity != null) {
            viewData.setChildrenCategory(categoryEntity);
            if (categoryEntity.getFatherId() != 0) {
                CategoryEntity f = categoryMap.get(categoryEntity.getFatherId());
                viewData.setFatherCategory(f);
            }
        }
    }


    public ArticleTextEntity getPasswordArticleTextEntity(long id, String password) {
        ArticleTextEntity byId = this.getById(id);
        if (byId.getPassword().equals(password) && byId.getType() == TypeCode.ARTICLE_TEXT_PASSWORD) {
            byId.setPassword("");
            return byId;
        }
        return null;
    }

    /**
     * 创建隐藏的文章视图（无权查看时返回）
     */
    private ArticleViewData createHiddenArticleView() {
        ArticleViewData articleViewData = new ArticleViewData();
        articleViewData.setIsShow(false);
        return articleViewData;
    }

    /**
     * 保存文章类型的稿件
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnCodeEnum saveText(TextArticleDto textArticleDto,
                                  HttpServletRequest request) {
        // 获取用户ID
        long userId = JwtUtil.getUserId(request);
        

        // 验证分类是否存在
        CategoryEntity categoryEntity = categoryCache.getCategoryEntityMap().get(textArticleDto.getCategory());
        if (categoryEntity == null) {
            return ReturnCodeEnum.CATEGORY_NOT_FOUND;
        }
        

        
        // 构建文章实体
        ArticleEntity articleEntity = buildArticleEntity(textArticleDto, userId, null);
        
        // 设置IP和UA信息
        String ip = ipUtil.getIpAddr(request);
        articleEntity.setIp(ip);
        articleEntity.setUa(ipUtil.getUa(request));
        articleEntity.setCity(ipUtil.getCity(ip));
        
        // 保存文章基本信息
        articleService.save(articleEntity);
        
        // 更新用户最后发布时间
        userService.updateLastPublishTime(System.currentTimeMillis(), userId);

        // 保存文章内容
        saveArticleTextList(textArticleDto.getTextList(), articleEntity.getId(), userId);
        
        return ReturnCodeEnum.SUCCESS;
    }
    
    /**
     * 构建文章实体
     */
    private ArticleEntity buildArticleEntity(TextArticleDto dto, long userId, FileTableEntity imageFile) {
        ArticleEntity article = new ArticleEntity();
        article.setTitle(dto.getTitle());
        article.setDescribes(dto.getDescribe());
        article.setCategory(dto.getCategory());
        // article.setImgUrl(imageFile.getFileUrl());
        article.setUserId(userId);

        
        long time = System.currentTimeMillis();
        article.setCreateTime(time);
        article.setUpdateTime(time);
        
        // 设置初始计数
        article.setViewCount(0L);
        article.setLikeCount(0L);
        article.setFavoriteCount(0L);
        article.setDislikeCount(0L);
        article.setCommentCount(0L);
        article.setDanmakuCount(0L);
        
        // 设置标签
        try {
            article.setTag(OBJECT_MAPPER.writeValueAsString(dto.getTag()));
        } catch (JsonProcessingException e) {
            log.warn("用户 {} 添加的文章标签序列化失败", userId);
        }
        
        // 设置审核状态
        if (webSettingCache.getWebConfigData().getOpenExamine()) {
            article.setExamineStatus(ExamineTypeEnum.PENDING_REVIEW.getCode());
        } else {
            article.setExamineStatus(ExamineTypeEnum.SUCCESS.getCode());
        }
        
        // 设置文章类型
        article.setType(FileTypeEnum.ARTICLE.getCode());
        article.setStatus(ArticleStatusEnum.NORMAL.getCode());
        if (dto.getImgUrl() == null || dto.getImgUrl().isEmpty()) {
            article.setImgUrl("");
        }
        
        return article;
    }
    

    /**
     * 保存文章内容列表
     */
    private void saveArticleTextList(List<ArticleTextEntity> textList, Long articleId, Long userId) {
        long currentTime = System.currentTimeMillis();
        List<ArticleTextEntity> saveList = new ArrayList<>(textList.size());

        // 用来收集链接
        List<String> linkList = new ArrayList<>();

        for (ArticleTextEntity text : textList) {
            text.setArticleId(articleId);
            text.setUserId(userId);
            text.setCreateTime(currentTime);
            text.setUpdateTime(currentTime);
            text.setStatus(ArticleStatusEnum.NORMAL.getCode()); // 设置状态为正常
            List<String> strings = MarkdownUtils.extractLinks(text.getContent());
            linkList.addAll(strings);
            // 如果没有设置类型，默认为普通文章
            if (text.getType() == null) {
                text.setType(TypeCode.ARTICLE_TEXT_NORMAL);
            }

            saveList.add(text);
        }
        
        // 批量保存文章内容
        this.saveBatch(saveList);

        // 分析链接内容
        if (!linkList.isEmpty()) {
            analyzeAndUpdateFileReferences(linkList, userId, articleId);
        }
    }
    
    /**
     * 分析链接并更新文件引用状态
     * 
     * @param linkList 链接列表
     * @param userId 用户ID
     * @param articleId 文章ID
     */
    private void analyzeAndUpdateFileReferences(List<String> linkList, Long userId, Long articleId) {
        // 先从所有链接中提取文件名
        List<String> fileNames = new ArrayList<>();
        for (String link : linkList) {
            String fileName = extractFileNameFromLink(link);
            if (fileName != null && !fileName.isEmpty()) {
                fileNames.add(fileName);
            }
        }
        
        // 如果没有有效的文件名，直接返回
        if (fileNames.isEmpty()) {
            return;
        }
        
        // 使用IN查询一次性获取所有匹配的文件
        QueryWrapper<FileTableEntity> wrapper =
                new QueryWrapper<>();
        wrapper.in("file_new_name", fileNames);
        // 只查询属于当前用户的文件
        wrapper.eq("upload_user_id", userId);
        // 只查询未使用的文件
        wrapper.ne("status", FileStatusEnum.USED.getCode());
        
        List<FileTableEntity> files = fileTableService.list(wrapper);
        
        // 更新文件状态
        if (!files.isEmpty()) {
            for (FileTableEntity file : files) {
                file.setStatus(FileStatusEnum.USED.getCode());
                file.setArticleId(articleId); // 关联到当前文章
            }
            
            // 批量更新文件状态
            fileTableService.updateBatchById(files);
        }
    }
    
    /**
     * 从链接中提取文件名
     * 
     * @param link 链接地址
     * @return 文件名
     */
    private String extractFileNameFromLink(String link) {
        if (link == null || link.isEmpty()) {
            return null;
        }
        
        // 处理形如 /ddd/ddd/dddd.mp4 的链接
        int lastSlashIndex = link.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < link.length() - 1) {
            return link.substring(lastSlashIndex + 1);
        }
        
        return link; // 如果没有斜杠，则返回整个链接作为文件名
    }
}
