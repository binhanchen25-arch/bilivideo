# Bilibili 视频网站后端

基于 Spring Boot 的仿 B 站视频网站后端项目，采用经典 MVC 分层架构，实现用户、视频、弹幕、关注等核心功能。

## 技术栈

- **框架**: Spring Boot 2.7.9
- **持久层**: MyBatis
- **数据库**: MySQL 8
- **缓存**: Redis
- **搜索引擎**: Elasticsearch
- **消息队列**: RocketMQ
- **文件存储**: FastDFS
- **实时通信**: WebSocket


## 项目结构

```
bilibili/
├── bilivideo-api      # 控制层：RESTful 接口
├── bilivideo-service  # 服务层：业务逻辑
└── bilivideo-dao      # 数据层：MyBatis Mapper
```

## 核心功能

| 模块 | 功能 |
|------|------|
| 用户 | 注册、登录（RSA 加密 + JWT）、用户信息、权限管理 |
| 视频 | 投稿、分页列表、在线播放（分片）、点赞/收藏/投币、评论、观看记录 |
| 弹幕 | 实时弹幕（WebSocket）、时间段筛选 |
| 社交 | 关注/粉丝、关注分组、用户动态 |
| 文件 | 分片上传、MD5 校验 |
| 推荐 | 基于 Mahout 的协同过滤推荐 |

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.x
- MySQL 8
- Redis
- Elasticsearch
- RocketMQ
- FastDFS（可选）

### 配置

修改 `bilivideo-service/src/main/resources/application-test.properties`：

- 数据库：`spring.datasource.*`
- Redis：`spring.redis.*`
- RocketMQ：`rocketmq.name.server.address`
- Elasticsearch：`elasticsearch.url`
- FastDFS：`fdfs.*`

### 启动

```bash
# 在 bilivideo-api 模块下运行
mvn spring-boot:run
```

主启动类：`com.imooc.ImoocBilibiliApp`

## 接口风格

采用 RESTful 设计，如：`GET /users`、`POST /user-tokens`、`PUT /users` 等。
