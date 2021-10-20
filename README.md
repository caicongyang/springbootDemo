# springBootDemo

### 软件说明

1. 基于springBoot的简单封装和测试
1. 来自日常工作总结和测试使用
1. 测试用例覆盖不够全面，不可直接用于生产环境

#### module 说明

0. core-starter ： 统一异常等核心功能 （done）
1. cache-starter ： 基于redis的缓存增强，想实现自动为key 加上工程名称，大key报错等安全防范功能
2. dubbo-mocker-starter:  基于dubbo的mock 功能
3. dynamic-datasource-starter： 多数据源实现
4. file-expand-starter： 文件功能增强 (doing)
5. tcc-starter： tcc分布式封装
6. httpclient-expand-starter： 基于hystrix对httpclient的增强  (done)
7. sklywalking-expand-starter： sklywalking功能增强,把sklywalking的traceId 和sklying地址放到http header 中
8. lock-starter: 基于redission和mysql 的分布式锁的简单封装  (done)
9. seq-starter： 基于数据库、雪花算法、redis的自增序列号简单封装
10. stock: demo 练习工程  (done)
11. local-test： 测试工程，所有的docker-compose文件都放在此处 （done）
12. git-tools： git批量操作
13. upload-starter : 云上传组件 （doing）
14. 基于rest 的两个mock功能



