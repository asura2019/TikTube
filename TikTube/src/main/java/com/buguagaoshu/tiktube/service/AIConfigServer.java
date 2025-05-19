package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.tiktube.cache.AIConfigCache;
import com.buguagaoshu.tiktube.cache.CountRecorder;
import com.buguagaoshu.tiktube.cache.WebSettingCache;
import com.buguagaoshu.tiktube.dao.AiConfigDao;
import com.buguagaoshu.tiktube.entity.AiConfigEntity;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.enums.TypeCode;
import com.buguagaoshu.tiktube.model.AIResult;
import com.buguagaoshu.tiktube.model.AiExamineCommentAndDanmakuResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-17
 */
@Service
@Slf4j
public class AIConfigServer extends ServiceImpl<AiConfigDao, AiConfigEntity> {

    private final WebConfigService webConfigService;

    private final WebSettingCache webSettingCache;

    private final CountRecorder countRecorder;

    @Autowired
    public AIConfigServer(WebConfigService webConfigService,
                          WebSettingCache webSettingCache,
                          CountRecorder countRecorder) {
        this.webConfigService = webConfigService;
        this.webSettingCache = webSettingCache;
        this.countRecorder = countRecorder;
    }

    public ReturnCodeEnum clousAiExamine(long userId, String ip) {
        var webConfig = webConfigService.initConfig();
        webConfig.setOpenAIConfig(false);
        webConfigService.saveWebConfig(webConfig, userId, ip);
        log.info("管理员 {} 关闭了了 AI 内容审核， IP地址为：{}", userId, ip);
        return ReturnCodeEnum.SUCCESS;
    }

    public ReturnCodeEnum openAIConfig(long userId, String ip) {
        if (this.webSettingCache.getWebConfigData().getOpenAIConfig()) {
            return ReturnCodeEnum.SUCCESS;
        }
        // 更新缓存
        setCacheConfig();
        // 查找可用 AI 配置
        if (AIConfigCache.aiConfigCache.isEmpty()) {
            return ReturnCodeEnum.NOO_FOUND;
        }
        // 更新设置
        var webConfig = webConfigService.initConfig();
        webConfig.setOpenAIConfig(true);
        webConfigService.saveWebConfig(webConfig, userId, ip);
        log.info("管理员 {} 开启了 AI 内容审核， IP地址为：{}", userId, ip);
        return ReturnCodeEnum.SUCCESS;
    }

    public boolean saveAIConfig(AiConfigEntity aiConfigEntity, long userId) {
        long time = System.currentTimeMillis();
        aiConfigEntity.setCreateTime(time);
        aiConfigEntity.setUpdateTime(time);
        aiConfigEntity.setUpdateUser(userId);
        aiConfigEntity.setCreateUser(userId);
        return this.save(aiConfigEntity);
    }

    public boolean updateAiConfig(AiConfigEntity aiConfigEntity, long userId) {
        long time = System.currentTimeMillis();
        AiConfigEntity byId = this.getById(aiConfigEntity.getId());
        BeanUtils.copyProperties(aiConfigEntity, byId);
        byId.setUpdateTime(time);
        byId.setUpdateUser(userId);
        return this.updateById(byId);
    }

    public List<AiConfigEntity> queryPage() {
        QueryWrapper<AiConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("update_time");
        List<AiConfigEntity> list = this.list(queryWrapper);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        list.forEach(aiConfigEntity -> {
            aiConfigEntity.setUseTokens(aiConfigEntity.getUseTokens() + countRecorder.getAiModelTokenCount(aiConfigEntity.getId()));
        });
        return list;
    }


    public void setCacheConfig() {
        log.info("开始读取 AI 配置");
        QueryWrapper<AiConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        List<AiConfigEntity> list = this.list(queryWrapper);

        if (list.isEmpty()) {
            log.info("未发现可用 AI 配置！系统将关闭 AI 服务！");
            webSettingCache.getWebConfigData().setOpenAIConfig(false);
            return;
        }

        Map<Long, AiConfigEntity> map = new HashMap<>();
        Map<Long, List<AiConfigEntity>> typeAICache = new HashMap<>();
        list.forEach(aiConfigEntity -> {
            map.put(aiConfigEntity.getId(), aiConfigEntity);
            // 根据type值对AI配置进行分类
            long type = aiConfigEntity.getType();
            if (!typeAICache.containsKey(type)) {
                typeAICache.put(type, new ArrayList<>());
            }
            typeAICache.get(type).add(aiConfigEntity);
        });
        AIConfigCache.aiConfigCache = map;
        // 将分类后的配置保存到缓存
        AIConfigCache.typeAICache = typeAICache;
        log.info("AI 配置读取完成");
    }


    public boolean deleteAI(AiConfigEntity aiConfigEntity) {
        // 获取 AI 缓存
        if (AIConfigCache.aiConfigCache.isEmpty()) {
            return this.removeById(aiConfigEntity.getId());
        } else {
            return false;
        }
    }

    /**
     * 向大模型发送请求的 方法
     * @param message 需要向 AI 提交的数据
     * */
    public AiExamineCommentAndDanmakuResult aiExamineCommentAndDanmaku(String message) {
        log.info("开始AI审核内容：");
        // 读取配置
        AiConfigEntity aiConfigEntity = AIConfigCache.getTypeConfig(TypeCode.AI_TYPE_EXAMINE_COMMENT, countRecorder.getAiModelTokenCountMap());
        if (aiConfigEntity == null) {
            return null;
        }
        // 构造客户端
        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(aiConfigEntity.getApiKey())
                .baseUrl(aiConfigEntity.getApiUrl())
                .build();
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(aiConfigEntity.getPrompt().replace("[用户输入内容]", message))
                .putAdditionalBodyProperty("enable_thinking", JsonValue.from(false))
                .model(aiConfigEntity.getModel())
                .build();
        // 结果
        ChatCompletion chatCompletion = client.chat().completions().create(params);
        long token = 0;
        String aiResult = chatCompletion.choices().get(0).message().content().orElse("");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AiExamineCommentAndDanmakuResult result = objectMapper.readValue(aiResult, AiExamineCommentAndDanmakuResult.class);
            token = chatCompletion.usage().get().totalTokens();
            result.setToken(token);
            result.setAiConfigId(aiConfigEntity.getId());
            log.info("AI审核结果如下：{}", result.getMessage());
            return result;
        } catch (Exception e) {
            return new AiExamineCommentAndDanmakuResult();
        }
    }
}
