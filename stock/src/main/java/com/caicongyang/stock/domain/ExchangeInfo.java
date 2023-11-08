package com.caicongyang.stock.domain;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
/**
 * Auto-generated: 2023-11-04 0:36:51
 *
 * @author www.jsons.cn 
 * @website http://www.jsons.cn/json2java/ 
 */
public class ExchangeInfo {

    private String timezone;
    @JsonProperty("serverTime")
    private int servertime;
    @JsonProperty("rateLimits")
    private List<Ratelimits> ratelimits;
    @JsonProperty("exchangeFilters")
    private List<String> exchangefilters;
    private List<Symbols> symbols;
    public void setTimezone(String timezone) {
         this.timezone = timezone;
     }
     public String getTimezone() {
         return timezone;
     }

    public void setServertime(int servertime) {
         this.servertime = servertime;
     }
     public int getServertime() {
         return servertime;
     }

    public void setRatelimits(List<Ratelimits> ratelimits) {
         this.ratelimits = ratelimits;
     }
     public List<Ratelimits> getRatelimits() {
         return ratelimits;
     }

    public void setExchangefilters(List<String> exchangefilters) {
         this.exchangefilters = exchangefilters;
     }
     public List<String> getExchangefilters() {
         return exchangefilters;
     }

    public void setSymbols(List<Symbols> symbols) {
         this.symbols = symbols;
     }
     public List<Symbols> getSymbols() {
         return symbols;
     }

}