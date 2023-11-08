package com.caicongyang.stock.domain;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
/**
 * Auto-generated: 2023-11-04 0:36:51
 *
 * @author www.jsons.cn 
 * @website http://www.jsons.cn/json2java/ 
 */
public class Filters {

    @JsonProperty("filterType")
    private String filtertype;
    @JsonProperty("minPrice")
    private String minprice;
    @JsonProperty("maxPrice")
    private String maxprice;
    @JsonProperty("tickSize")
    private String ticksize;
    public void setFiltertype(String filtertype) {
         this.filtertype = filtertype;
     }
     public String getFiltertype() {
         return filtertype;
     }

    public void setMinprice(String minprice) {
         this.minprice = minprice;
     }
     public String getMinprice() {
         return minprice;
     }

    public void setMaxprice(String maxprice) {
         this.maxprice = maxprice;
     }
     public String getMaxprice() {
         return maxprice;
     }

    public void setTicksize(String ticksize) {
         this.ticksize = ticksize;
     }
     public String getTicksize() {
         return ticksize;
     }

}