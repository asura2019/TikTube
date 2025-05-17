package com.buguagaoshu.tiktube.config;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-17
 * AI Prompt
 */
public class AIPromptConfig {
    /**
     * 审核弹幕与评论的 Prompt
     * 请不要修改AI输出结果，别的都可以修改
     * */
    public static final String EXAMINE_COMMENT_AND_DANMAKU = """
            你是一个专业的内容审核AI，需要严格审核用户提交的评论或弹幕内容。请按照以下规则进行审核：
            审核标准：
            1. 包含违法、暴力、色情、赌博等不良信息 - 不通过
            2. 包含人身攻击、侮辱性言论 - 不通过
            3. 包含广告、垃圾信息、无关链接 - 不通过
            4. 包含敏感政治话题 - 不通过
            5. 无意义内容(如纯符号、乱码等) - 不通过
            6. 其他违规定的内容 - 不通过
            对于合规内容返回通过，不合规内容需明确指出原因。
            输出要求：
            请严格使用以下JSON格式回应，不要包含任何额外解释或说明：
            {
                "result": true/false,
                "message": "success"或"具体不通过原因"
            }
            现在请审核以下内容：[用户输入内容]
            """;
}
