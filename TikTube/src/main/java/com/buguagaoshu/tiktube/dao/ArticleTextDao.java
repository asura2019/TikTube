package com.buguagaoshu.tiktube.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buguagaoshu.tiktube.entity.ArticleTextEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 保存与稿件关联的文章
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 */
@Mapper
public interface ArticleTextDao extends BaseMapper<ArticleTextEntity> {

}