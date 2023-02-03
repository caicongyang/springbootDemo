package com.caicongyang.sklywalking.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;


@ConfigurationProperties(prefix = "trace")
public class TraceProperties {

    private Set<String> excludePatterns = new HashSet<>();

    private boolean enable = true;

    /**
     * http://trace.caicongyang.com/trace?traceid=@{traceId}
     */
    private String skywalkingUrl;

    public String getSkywalkingUrl() {
        return skywalkingUrl;
    }

    public void setSkywalkingUrl(String skywalkingUrl) {
        this.skywalkingUrl = skywalkingUrl;
    }

    public boolean getEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Set<String> getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(Set<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }
}
