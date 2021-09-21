package com.caicongyang.client.domain;

/**
 * Created by ZhouChenmin on 2018/1/11.
 */
public class BaseUploadResult {


    private int failCount;
    private int totalCount;
    private int totalCostTime;
    private int successCount;

    private String exception;


    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalCostTime() {
        return totalCostTime;
    }

    public void setTotalCostTime(int totalCostTime) {
        this.totalCostTime = totalCostTime;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
