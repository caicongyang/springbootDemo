package com.caicongyang.client.domain;

import java.util.Map;

/**
 * Created by ZhouChenmin on 2018/1/11.
 */
public class BatchUrlUploadResult extends BaseUploadResult{


    /**
     * key 为 上传时指定的url
     * value 为 上传后的结果
     */
    private Map<String,ItemResult> resultDetail;


    public Map<String, ItemResult> getResultDetail() {
        return resultDetail;
    }

    public void setResultDetail(Map<String, ItemResult> resultDetail) {
        this.resultDetail = resultDetail;
    }
}
