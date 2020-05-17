package com.caicongyang.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caicongyang.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SevenMoorWorkOrderDTO implements Serializable {



    private String _id;
    private String customerId;
    private String user;
    private List<String> fields;
    private String createTime;
    private long createTimestamp;
    private String action;
    private String stepName;
    private String businessNumber;
    private String flowInfo;
    private List<Map<String,Object>> stepFields;
    public void set_id(String _id) {
        this._id = _id;
    }
    public String get_id() {
        return _id;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public String getCustomerId() {
        return customerId;
    }

    public void setUser(String user) {
        this.user = user;
    }
    public String getUser() {
        return user;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
    public List<String> getFields() {
        return fields;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }
    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setAction(String action) {
        this.action = action;
    }
    public String getAction() {
        return action;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }
    public String getStepName() {
        return stepName;
    }

    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }
    public String getBusinessNumber() {
        return businessNumber;
    }

    public void setFlowInfo(String flowInfo) {
        this.flowInfo = flowInfo;
    }
    public String getFlowInfo() {
        return flowInfo;
    }

    public List<Map<String, Object>> getStepFields() {
        return stepFields;
    }

    public void setStepFields(List<Map<String, Object>> stepFields) {
        this.stepFields = stepFields;
    }

    public static void main(String[] args) {

        String jsonString  ="{\"_id\":\"7f4ef120-9378-11ea-8e4b-439207a9e1ca\",\"customerId\":\"df354a00-9001-11ea-a6e8-074bf3f2475e\",\"user\":\"8000\",\"fields\":[],\"createTime\":\"2020-05-11 19:13:52\",\"createTimestamp\":1589195632000,\"action\":\"下一步处理\",\"stepName\":\"开始步骤\",\"businessNumber\":\"2020051100002\",\"flowInfo\":\"工单名称:test,客户名称:,客户账号/手机号/企业名称:test,问题描述:test,工单类型:[举报投诉,举报],附件: ,执行人:[张磊/zhangl]\",\"stepFields\":[{\"name\":\"工单名称\",\"type\":\"single\",\"value\":\"test\"},{\"name\":\"客户名称\",\"type\":\"single\",\"value\":\"\"},{\"name\":\"客户账号/手机号/企业名称\",\"type\":\"multi\",\"value\":\"test\"},{\"name\":\"问题描述\",\"type\":\"multi\",\"value\":\"test\"},{\"name\":\"工单类型\",\"type\":\"dropdown\",\"value\":[\"举报投诉\",\"举报\"]},{\"name\":\"附件\",\"type\":\"file\",\"value\":[]},{\"name\":\"执行人\",\"type\":\"dropdown\",\"value\":[\"张磊/zhangl\"]}]}";

        JSONObject jsonObject = JSON.parseObject(jsonString);

        JSONArray stepFields = jsonObject.getJSONArray("stepFields");

        String workOrderType = StringUtils.EMPTY;
        String workOrderCustomerName = StringUtils.EMPTY;
        String workOrderTitle = StringUtils.EMPTY;
        String workOrderCurrUser = StringUtils.EMPTY;

        for (int i = 0; i < stepFields.size(); i++) {
            JSONObject field = stepFields.getJSONObject(i);

            if (field.getString("name").equalsIgnoreCase("工单类型")) {
                workOrderType = field.getString("value");
            }
            if (field.getString("name").equalsIgnoreCase("客户账号/手机号/企业名称")) {
                workOrderCustomerName = field.getString("value");
            }
            if (field.getString("name").equalsIgnoreCase("工单名称")) {
                workOrderTitle = field.getString("value");
            }
            if (field.getString("name").equalsIgnoreCase("工单名称")) {
                workOrderTitle = field.getString("value");
            }

            if (field.getString("name").equalsIgnoreCase("执行人")) {
                JSONArray values = field.getJSONArray("value");
                workOrderCurrUser =values.get(0).toString();
            }


        }

        System.out.println(workOrderCurrUser.split("/")[1]);

        System.out.printf("");
    }
}
