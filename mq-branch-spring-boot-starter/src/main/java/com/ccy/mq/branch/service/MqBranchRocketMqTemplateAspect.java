package com.ccy.mq.branch.service;

import com.alibaba.fastjson.JSONObject;
import com.ccy.mq.branch.context.MqBranchContextHolder;
import com.ccy.mq.branch.util.RocketMqBranchRegexUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Aspect
public class MqBranchRocketMqTemplateAspect {

    private static final Logger logger = LoggerFactory.getLogger(MqBranchRocketMqTemplateAspect.class);


    @Value("${git.branch:master}")
    private String branchLocal;

    @Autowired
    private MqBranchRocketMqGroupJob mqBranchRocketMqGroupJob;


    // 定义切点，匹配MessageSender类中方法名包含"send"的方法
    @Pointcut("execution(* org.apache.rocketmq.spring.core.RocketMQTemplate.send*(..)) || execution(* org.apache.rocketmq.spring.core.RocketMQTemplate.*Send*(..))")
    public void sendMessageMethods() {
    }

    // 在方法执行前拦截
    @Around("sendMessageMethods()")
    public Object modifyTopic(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Intercepted method before send: " + joinPoint.getSignature().getName());
        //获取消费者group
        Set<String> groupNameSet = mqBranchRocketMqGroupJob.getGroupNameSet();
        if (CollectionUtils.isEmpty(groupNameSet)) {
            groupNameSet = mqBranchRocketMqGroupJob.refreshGroupList();
        }
        List<String> topicList = groupNameSet.stream().map(a -> a.substring(a.indexOf("*") + 1)).collect(Collectors.toList());


        //获取自身branch
        String branch = this.getBranch();
        branch = RocketMqBranchRegexUtil.getLegalStr(branch);
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < args.length; i++) {
            if (parameterNames[i].equals("destination")) {
                logger.info("拦截topic:{}", args[i]);
                String branchTopic = args[i] + "_" + branch.toUpperCase();
                String masterBranchTopic = args[i] + "_MASTER";
                //如果有带分支的group则修改topic参数,如果没有判断master,再没有走原来topic
                if (topicList.contains(branchTopic)) {
                    args[i] = branchTopic;
                } else if (topicList.contains(masterBranchTopic)) {
                    args[i] = masterBranchTopic;
                }
                logger.info("branchTopic:{},finalTopic:{}", branchTopic, args[i]);
            }
            if (parameterNames[i].equals("payload")) {
                Object payload = args[i];
                JSONObject jsonObject = null;
                if (payload instanceof String) {
                    jsonObject = JSONObject.parseObject((String) payload);
                } else {
                    jsonObject = (JSONObject) JSONObject.toJSON(payload);
                }
                jsonObject.put("branch", branch);
                args[i] = jsonObject;
                logger.debug("branch:{}", branch);
            }
        }
        return joinPoint.proceed(args);
    }

    private String getBranch() {
        //默认master
        String branch = "master";
        //先取消息带过来的分支
        if (StringUtils.isNotBlank(MqBranchContextHolder.get())) {
            branch = MqBranchContextHolder.get();
            return branch;
        }

        //再取自己本身打包的分支
        return branchLocal;
    }


    // 定义切点，匹配消息接收的方法
    @Pointcut("execution(* org.apache.rocketmq.spring.core.RocketMQListener.onMessage(..))")
    public void receiveMessageMethods() {
    }

    @Around("receiveMessageMethods()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        // 在目标方法执行前的逻辑
        Object[] args = joinPoint.getArgs();
        MessageExt message = (MessageExt) args[0];
        JSONObject mqJsonObject = JSONObject.parseObject(message.getBody(), JSONObject.class);
        String branch = mqJsonObject.getString("branch");
        if (StringUtils.isNotBlank(branch)) {
            MqBranchContextHolder.set(branch);
            logger.info("线程：{}，塞入mqBranch:{}", Thread.currentThread().getId(), branch);
        }

        // 调用目标方法
        Object result = joinPoint.proceed();

        // 在目标方法执行后的逻辑
        logger.info("线程：{}，清除mqBranch:{}", Thread.currentThread().getId(), MqBranchContextHolder.get());
        MqBranchContextHolder.remove();
        // 返回目标方法的结果
        return result;
    }

}