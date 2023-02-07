# springBootDemo

### 软件说明

1.基于springBoot的简单封装和测试
2.来自日常工作总结和测试使用
3.测试用例覆盖不够全面，不可直接用于生产环境

#### module 说明

1. cache-starter ： 基于redis的缓存增强，想实现自动为key 加上工程名称，大key报错等安全防范功能 (done)
2. core-starter ： 统一异常等核心功能 （done）
3. dubbo-mocker-starter:  基于dubbo的mock 功能
4. dynamic-datasource-starter： 多数据源实现
5. encrypt-starter: 数据字段加解密，字段脱敏等等
6. feign-expand-starter:  feign拓展，自动生成feignClient 等
7. file-expand-starter： 文件功能增强 (doing)
8. git-tools： git批量操作
9. httpclient-expand-starter： 基于hystrix对httpclient的增强  (done)
10. local-test： 测试工程，所有的docker-compose文件都放在此处 （done）
11. lock-starter: 基于redission和mysql 的分布式锁的简单封装  (done)
12. logger-starter: 日志关键字脱敏 &  日志发送到kafka，从而进到elastic-search
13. rest-mock-starter:基于rest 的mock功能,方便测试&本地联调
14. seq-starter： 基于数据库、雪花算法、redis的自增序列号简单封装
15. sklywalking-expand-starter： sklywalking功能增强,把sklywalking的traceId 和sklying地址放到http header 中 ；上报sql 执行时间  (done)
16. stock: demo 练习工程  (done)
17. tcc-starter： tcc分布式封装
18. upload-starter : 云上传组件 （doing）



