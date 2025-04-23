package com.buguagaoshu.tiktube.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.buguagaoshu.tiktube.cache.PlayCountRecorder;
import com.buguagaoshu.tiktube.entity.ArticleEntity;
import com.buguagaoshu.tiktube.entity.FileTableEntity;
import com.buguagaoshu.tiktube.entity.PlayRecordingEntity;
import com.buguagaoshu.tiktube.service.ArticleService;
import com.buguagaoshu.tiktube.service.FileTableService;
import com.buguagaoshu.tiktube.service.PlayRecordingService;
import com.buguagaoshu.tiktube.service.PlayRecordingWithArticleService;
import com.buguagaoshu.tiktube.utils.IpUtil;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.vo.PlayRecordingWithArticleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2022-05-22 16:21
 */
@Service
public class PlayRecordingWithArticleServiceImpl implements PlayRecordingWithArticleService {
    private final PlayRecordingService playRecordingService;

    private final ArticleService articleService;

    private final FileTableService fileTableService;

    private final IpUtil ipUtil;


    @Autowired
    public PlayRecordingWithArticleServiceImpl(PlayRecordingService playRecordingService,
                                               ArticleService articleService,
                                               FileTableService fileTableService,
                                               IpUtil ipUtil) {
        this.playRecordingService = playRecordingService;
        this.articleService = articleService;
        this.fileTableService = fileTableService;
        this.ipUtil = ipUtil;
    }


    @Override
    public PageUtils playRecordingList(Map<String, Object> params, HttpServletRequest request) {
        IPage<PlayRecordingEntity> page = playRecordingService.queryPage(params, request);
        Set<Long> aids = page.getRecords().stream().map(PlayRecordingEntity::getArticleId).collect(Collectors.toSet());
        List<ArticleEntity> articleEntities = articleService.listByIds(aids);
        List<PlayRecordingWithArticleVo> play = new LinkedList<>();
        if (articleEntities != null && !articleEntities.isEmpty()) {
            Map<Long, ArticleEntity> articleEntityMap =  articleEntities.stream().collect(Collectors.toMap(ArticleEntity::getId, a->a));

            for (PlayRecordingEntity playRecordingEntity : page.getRecords()) {
                PlayRecordingWithArticleVo playRecordingWithArticleVo = new PlayRecordingWithArticleVo();
                playRecordingWithArticleVo.setPlayRecordingEntity(playRecordingEntity);


                ArticleEntity articleEntity = articleEntityMap.get(playRecordingEntity.getArticleId());

                playRecordingWithArticleVo.setArticleEntity(articleEntity);
                play.add(playRecordingWithArticleVo);
            }
        }
        return new PageUtils(play, page.getTotal(), page.getSize(), page.getCurrent());
    }

    @Override
    public String savePlayLog(PlayRecordingEntity playRecording, HttpServletRequest request) {
        long userId = JwtUtil.getUserId(request);

        if (userId == playRecording.getUserId()) {
            FileTableEntity videoFile = fileTableService.getById(playRecording.getVideoId());
            if (videoFile == null) {
                return "视频信息错误";
            }
            if (playRecording.getArticleId().equals(videoFile.getArticleId())) {
                PlayRecordingEntity nowLog =
                        playRecordingService.findPlayRecordingEntityByArticleIdAndVideoId(videoFile.getArticleId(), videoFile.getId(), userId);
                if (nowLog == null) {
                    if (playRecording.getCreateTime() == null) {
                        playRecording.setCreateTime(System.currentTimeMillis());
                    }
                    playRecording.setUpdateTime(System.currentTimeMillis());
                    playRecording.setUa(ipUtil.getUa(request));
                    playRecordingService.save(playRecording);
                } else {
                    nowLog.setUa(ipUtil.getUa(request));
                    nowLog.setVideoTime(playRecording.getVideoTime());
                    nowLog.setUpdateTime(System.currentTimeMillis());
                    playRecordingService.updateById(nowLog);
                }
                return "success";
            }
            return "no power";
        } else {
            return "用户信息错误";
        }
    }
}
