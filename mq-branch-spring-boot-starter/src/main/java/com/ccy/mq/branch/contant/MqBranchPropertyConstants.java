package com.ccy.mq.branch.contant;

public interface MqBranchPropertyConstants {
    //是否开启mq分支隔离
    String MQ_BRANCH_ENABLE = "mq.branch.enable";
    //分支隔离类型
    String MQ_BRANCH_TYPE = "mq.branch.type";
    //分支隔离类型-rocketMQ
    String MQ_BRANCH_SUPPORT_ROCKET_MQ = "rocketMQ";
    //GIT信息文件获取地址
    String GIT_PATH = "spring.application.git.generator.path";
}
