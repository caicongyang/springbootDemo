package com.ccy.mq.branch.entity;

import java.util.List;

public class RocketMqConsumerGroupResponse {
    private int status;
    private List<RocketMqConsumerGroup> data;
    private String errMsg;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setData(List<RocketMqConsumerGroup> data) {
        this.data = data;
    }

    public List<RocketMqConsumerGroup> getData() {
        return data;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrMsg() {
        return errMsg;
    }


    public class RocketMqConsumerGroup {

        private String group;
        private String version;
        private int count;
        private String consumeType;
        private String messageModel;
        private int consumeTps;
        private int diffTotal;

        public void setGroup(String group) {
            this.group = group;
        }

        public String getGroup() {
            return group;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getVersion() {
            return version;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public void setConsumeType(String consumeType) {
            this.consumeType = consumeType;
        }

        public String getConsumeType() {
            return consumeType;
        }

        public void setMessageModel(String messageModel) {
            this.messageModel = messageModel;
        }

        public String getMessageModel() {
            return messageModel;
        }

        public void setConsumeTps(int consumeTps) {
            this.consumeTps = consumeTps;
        }

        public int getConsumeTps() {
            return consumeTps;
        }

        public void setDiffTotal(int diffTotal) {
            this.diffTotal = diffTotal;
        }

        public int getDiffTotal() {
            return diffTotal;
        }

    }
}
