package com.caicongyang.stock.domain;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
/**
 * Auto-generated: 2023-11-04 0:36:51
 *
 * @author www.jsons.cn 
 * @website http://www.jsons.cn/json2java/ 
 */
public class Ratelimits {

    @JsonProperty("rateLimitType")
    private String ratelimittype;
    private String interval;
    @JsonProperty("intervalNum")
    private int intervalnum;
    private int limit;
    public void setRatelimittype(String ratelimittype) {
         this.ratelimittype = ratelimittype;
     }
     public String getRatelimittype() {
         return ratelimittype;
     }

    public void setInterval(String interval) {
         this.interval = interval;
     }
     public String getInterval() {
         return interval;
     }

    public void setIntervalnum(int intervalnum) {
         this.intervalnum = intervalnum;
     }
     public int getIntervalnum() {
         return intervalnum;
     }

    public void setLimit(int limit) {
         this.limit = limit;
     }
     public int getLimit() {
         return limit;
     }

}