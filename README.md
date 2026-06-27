# springBootDemo

### 软件说明

1. 基于 Spring Boot 3.4.13 / Java 21 的各类 starter 封装和测试
2. 来自日常工作学习总结和测试使用
3. 测试用例覆盖不够全面，不可直接用于生产环境
4. 每个 module 中还有 README.md 方便查看用法 & 其他更优开源组件推荐（待补充）

### 技术栈

- **Java 21**, **Spring Boot 3.4.13**, **Spring Cloud 2024.0.1**
- Dubbo 3.3.4, MyBatis-Plus 3.5.10, Redisson 3.45.1, RocketMQ 5.3.2
- SkyWalking 9.3.0, Logback 1.5.18, HttpClient 5

### Module 说明

| # | 模块 | 说明 | 状态 |
|---|------|------|------|
| 1 | apollo-config-starter | 基于 Apollo 配置中心的动态配置刷新 & Logback 日志级别热更新 | ✅ done |
| 2 | cache-starter | 基于 Redis 的缓存增强：自动 key 前缀（应用名）、大 key 报警（10KB阈值）、Set 容量限制 | ✅ done |
| 3 | core-starter | 统一异常处理、请求日志切面（记录参数/耗时/慢请求检测）、接口防重/限流（Redis Lua）、XSS 防护 | ✅ done |
| 4 | dubbo-mock-starter | 基于 Dubbo 3.x 的 Mock 功能，将接口指向测试人员搭建的指定地址 | ✅ done |
| 5 | encrypt-starter | 数据字段 AES-128 加解密 + 敏感信息脱敏（手机/邮箱/身份证自动识别），Jackson 序列化集成 | ✅ done |
| 6 | feign-expand-starter | Feign 扩展：`@EnableFeignExpanded` 注解 + `FeignClientFactory` 运行时代理创建 | ✅ done |
| 7 | file-expand-starter | 文件功能增强：Excel 导入导出（POI 5.4）、PDF 生成（JasperReports 7 + Freemarker）、通用文件上传下载工具 | ✅ done |
| 8 | git-tools | Git 批量操作（JGit 6.10）：批量 clone/pull/status/branch，配置文件和目录发现两种模式 | ✅ done |
| 9 | httpclient-expand-starter | 基于 HttpClient 5 的 HTTP 客户端封装，支持连接池、JSON/表单提交 | ✅ done |
| 10 | local-test | 测试工程，所有 docker-compose 文件（MySQL/Redis）放此处 | ✅ done |
| 11 | lock-starter | 分布式锁封装：Redis（Redisson）+ MySQL 两种实现 | ✅ done |
| 12 | logger-starter | 日志关键字脱敏 + 日志发送到 Kafka → ElasticSearch（Logback Appender） | ✅ done |
| 13 | mq-branch-spring-boot-starter | RocketMQ 分支隔离 Starter：多分支环境下 Topic/ConsumerGroup 自动隔离，避免消息互相干扰 | ✅ done |
| 14 | readwrite-datasource-starter | 数据源读写分离 + 注解驱动的读写路由 | ✅ done |
| 15 | rest-mock-starter | 基于 RestTemplate 的 Mock 功能，URL 替换拦截器，方便本地联调 | ✅ done |
| 16 | seq-starter | 分布式序列号：雪花算法（默认）+ 数据库序列表 + Redis INCR，通过 `seq.type` 切换 | ✅ done |
| 17 | shutdown-graceful-nacos-starter | 基于 Nacos 的微服务优雅上下线（LoadBalancer 缓存刷新） | ✅ done |
| 18 | sklywalking-expand-starter | SkyWalking 增强：TraceId/SkyWalking 地址注入 HTTP Header、MyBatis SQL 耗时上报、Feign/RestTemplate 拦截 | ✅ done |
| 19 | tcc-starter | 轻量级 TCC 分布式事务框架：`@TccTransaction` AOP Try-Confirm-Cancel 编排 | ✅ done |
| 20 | upload-starter | 云上传组件（七牛云 SDK） | ✅ done |

### 构建与测试

```bash
# 全量编译（跳过 local-test 集成测试）
mvn clean compile -pl '!local-test'

# 运行所有测试
mvn test -pl '!local-test'

# 构建单个模块
mvn -pl cache-starter -am clean install
```

**测试覆盖：114 个测试用例，10 个模块，全部通过。**
