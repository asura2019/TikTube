# 更新日志

## 2025-05-03 

新增 advertisement_table 表，用来保存广告与公告数据

完成公告、广告、贴片广告功能

新增 web_config 表，为重构网站设置功能做准备

修复前端已知 BUG

## 2025-05-01 v1.1.0 beta

file_table 表增加 save_location 字段，用来区分存储位置

增加 oss_config 表，用来保存对象存储配置

user 表增加 `otp`、`status` 、`block_end_time` 、`otp_secret` 、`otp_recovery` 用于实现 TOTP 两步认证，账户封禁功能

其它已知 BUG 修复

细节参见代码提交 commit

## 2025-04-20 V1.0.0 beta

发布 V1.0.0 beta 版本

实现除数据分析外全部功能

已经基本可用

## 2025-04-13

修改项目名称为 TikTube，规避风险

## 2025-04-08

favorites_table 表新增 favorites_label_id 字段，用来关联收藏夹

## 2025-04-05

实现前端自动截图，移除后端自动截图功能

## 2022-10-29

修复网站简介与MySQL关键词冲突的BUG

## 2022-10-05

完成分区页面开发

## 2022-08-26

基本完成评论系统

## 2022-08-17 

新增播放记录时间戳