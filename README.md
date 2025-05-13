# springBootDemo

### 软件说明

1.基于springBoot的各类starter简单封装和测试
2.来自日常工作学习总结和测试使用
3.测试用例覆盖不够全面，不可直接用于生产环境
4.每个module中还有README.md方便查看用法& 以及其他更优开源组件推荐 （待补充）

#### module 说明
1. apollo-config-starter: 基于apolo配置中的动态更新配置&loggerback日志级别修改  (done)
2. cache-starter ： 基于redis的缓存增强，想实现自动为key 加上工程名称，大key报错等安全防范功能 (done)
3. core-starter ： 统一异常等核心功能 （done）
4. dubbo-mocker-starter:  基于dubbo 2.7.x的mock 功能，将某个接口指向配置的测试人员搭建的某个地址 (done)
5. encrypt-starter: 数据字段加解密，字段脱敏等等 
6. feign-expand-starter:  feign拓展，自动生成feignClient 等 
7. file-expand-starter： 文件功能增强 (doing)
8. git-tools： git批量操作 
9. httpclient-expand-starter： 基于hystrix对httpclient的增强  (done)
10. local-test： 测试工程，所有的docker-compose文件都放在此处 （done） 
11. lock-starter: 基于redission和mysql 的分布式锁的简单封装  (done)
12. logger-starter: 日志关键字脱敏 &  日志发送到kafka，从而进到elastic-search  (done)
13. mq-branch-spring-boot-starter: 是一个针对 RocketMQ 消息队列的分支隔离 Spring Boot Starter，主要提供在多分支开发环境中对 RocketMQ 的主题（Topic）和消费组（ConsumerGroup）进行隔离的能力，避免不同分支之间的消息互相干扰。
13. shutdown-gracefule-nacos-starter: 基于nacos 服务实现的微服务优雅上下线 (done)
13. readwrite-datasource-starter： 数据源读写分离实现，并且实现了根据注解对文件目录进行读写分离  (done)
14. rest-mock-starter:基于rest 的mock功能,方便测试&本地联调   (done)
15. seq-starter： 基于数据库、雪花算法、redis的自增序列号简单封装 
16. shutdown-gracefule-nacos-starter: 基于nacos 服务实现的微服务优雅上下线 (done)
17. sklywalking-expand-starter： sklywalking功能增强,把sklywalking的traceId 和sklying地址放到http header 中 ；上报sql 执行时间  (done)
18. stock: demo 练习工程  (done)
19. tcc-starter： tcc分布式封装 
20. upload-starter : 云上传组件 （done）



