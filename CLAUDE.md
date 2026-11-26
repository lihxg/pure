# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

`pure-root` 是一个 Spring Boot 4 (Maven) 微服务项目，当前处于脚手架阶段，仅有一个空应用入口和一个冒烟测试。项目意图：集成 Spring AI 调用 DeepSeek 模型，并接入 MySQL 持久化。

## 构建与测试

使用 Maven Wrapper，无需本地安装 Maven：

```bash
./mvnw compile              # 编译
./mvnw test                 # 运行全部测试
./mvnw test -Dtest=PureRootApplicationTests   # 运行单个测试类
./mvnw spring-boot:run      # 启动应用
```

## 技术栈

- **Java 17**，Spring Boot `4.1.0`
- **Spring AI `2.0.0`**：`spring-ai-starter-model-deepseek`（DeepSeek 对话模型）
- **MySQL**：`mysql-connector-j`（runtime scope，当前仅引入驱动，尚无 JPA/JDBC 数据访问层）
- **Lombok**：已配置 annotation processor
- 测试：`spring-boot-starter-webmvc-test`（JUnit 5 + Spring Boot Test）

## 架构要点

- 入口：`src/main/java/com/pure/pureroot/PureRootApplication.java`
- 配置：`src/main/resources/application.properties`（目前仅 `spring.application.name`）
- Web 依赖使用 **`spring-boot-starter-webmvc`**（Spring Boot 4 的命名），而非旧版 `spring-boot-starter-web`
- 代码风格、测试规范遵循用户全局约定（Java 用 tab 缩进、JUnit 跑测试），包结构 `com.pure.pureroot`