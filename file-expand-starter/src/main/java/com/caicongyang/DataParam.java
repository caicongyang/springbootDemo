package com.caicongyang;

import java.util.Map;

public abstract class DataParam {

    private Map<String, Object> parameters;

    public DataParam() {
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

}
