package com.caicongyang.client.domain;

import java.util.List;

/**
 * Created by pengrongxin on 2017/3/1.
 */
public class UploadResult extends BaseUploadResult{

    private List<ItemResult> resultDetail;

    public List<ItemResult> getResultDetail() {
        return resultDetail;
    }

    public void setResultDetail(List<ItemResult> resultDetail) {
        this.resultDetail = resultDetail;
    }


}
