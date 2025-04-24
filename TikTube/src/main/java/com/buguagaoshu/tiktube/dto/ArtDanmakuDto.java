package com.buguagaoshu.tiktube.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-04
 */
@Data
public class ArtDanmakuDto {
    /**
     * 弹幕作者
     * */
    private String author;

    /**
     * 弹幕颜色
     * */
    @Size(max = 100)
    private String color;

    /**
     * 弹幕所属视频文件的 ID
     * */
    private Long id;

    /**
     * 正文
     * */
    @Size(max = 100, message = "弹幕最大长度不能超过 100 字！")
    private String text;

    /**
     * 时间
     * */
    private Double time;

    /**
     * 类型
     * */
    private Integer type;
}
