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
public class Symbols {

    private String symbol;
    private String status;
    @JsonProperty("baseAsset")
    private String baseasset;
    @JsonProperty("baseAssetPrecision")
    private int baseassetprecision;
    @JsonProperty("quoteAsset")
    private String quoteasset;
    @JsonProperty("quotePrecision")
    private int quoteprecision;
    @JsonProperty("quoteAssetPrecision")
    private int quoteassetprecision;
    @JsonProperty("baseCommissionPrecision")
    private int basecommissionprecision;
    @JsonProperty("quoteCommissionPrecision")
    private int quotecommissionprecision;
    @JsonProperty("orderTypes")
    private List<String> ordertypes;
    @JsonProperty("icebergAllowed")
    private boolean icebergallowed;
    @JsonProperty("ocoAllowed")
    private boolean ocoallowed;
    @JsonProperty("quoteOrderQtyMarketAllowed")
    private boolean quoteorderqtymarketallowed;
    @JsonProperty("allowTrailingStop")
    private boolean allowtrailingstop;
    @JsonProperty("cancelReplaceAllowed")
    private boolean cancelreplaceallowed;
    @JsonProperty("isSpotTradingAllowed")
    private boolean isspottradingallowed;
    @JsonProperty("isMarginTradingAllowed")
    private boolean ismargintradingallowed;
    private List<Filters> filters;
    private List<String> permissions;
    @JsonProperty("defaultSelfTradePreventionMode")
    private String defaultselftradepreventionmode;
    @JsonProperty("allowedSelfTradePreventionModes")
    private List<String> allowedselftradepreventionmodes;
    public void setSymbol(String symbol) {
         this.symbol = symbol;
     }
     public String getSymbol() {
         return symbol;
     }

    public void setStatus(String status) {
         this.status = status;
     }
     public String getStatus() {
         return status;
     }

    public void setBaseasset(String baseasset) {
         this.baseasset = baseasset;
     }
     public String getBaseasset() {
         return baseasset;
     }

    public void setBaseassetprecision(int baseassetprecision) {
         this.baseassetprecision = baseassetprecision;
     }
     public int getBaseassetprecision() {
         return baseassetprecision;
     }

    public void setQuoteasset(String quoteasset) {
         this.quoteasset = quoteasset;
     }
     public String getQuoteasset() {
         return quoteasset;
     }

    public void setQuoteprecision(int quoteprecision) {
         this.quoteprecision = quoteprecision;
     }
     public int getQuoteprecision() {
         return quoteprecision;
     }

    public void setQuoteassetprecision(int quoteassetprecision) {
         this.quoteassetprecision = quoteassetprecision;
     }
     public int getQuoteassetprecision() {
         return quoteassetprecision;
     }

    public void setBasecommissionprecision(int basecommissionprecision) {
         this.basecommissionprecision = basecommissionprecision;
     }
     public int getBasecommissionprecision() {
         return basecommissionprecision;
     }

    public void setQuotecommissionprecision(int quotecommissionprecision) {
         this.quotecommissionprecision = quotecommissionprecision;
     }
     public int getQuotecommissionprecision() {
         return quotecommissionprecision;
     }

    public void setOrdertypes(List<String> ordertypes) {
         this.ordertypes = ordertypes;
     }
     public List<String> getOrdertypes() {
         return ordertypes;
     }

    public void setIcebergallowed(boolean icebergallowed) {
         this.icebergallowed = icebergallowed;
     }
     public boolean getIcebergallowed() {
         return icebergallowed;
     }

    public void setOcoallowed(boolean ocoallowed) {
         this.ocoallowed = ocoallowed;
     }
     public boolean getOcoallowed() {
         return ocoallowed;
     }

    public void setQuoteorderqtymarketallowed(boolean quoteorderqtymarketallowed) {
         this.quoteorderqtymarketallowed = quoteorderqtymarketallowed;
     }
     public boolean getQuoteorderqtymarketallowed() {
         return quoteorderqtymarketallowed;
     }

    public void setAllowtrailingstop(boolean allowtrailingstop) {
         this.allowtrailingstop = allowtrailingstop;
     }
     public boolean getAllowtrailingstop() {
         return allowtrailingstop;
     }

    public void setCancelreplaceallowed(boolean cancelreplaceallowed) {
         this.cancelreplaceallowed = cancelreplaceallowed;
     }
     public boolean getCancelreplaceallowed() {
         return cancelreplaceallowed;
     }

    public void setIsspottradingallowed(boolean isspottradingallowed) {
         this.isspottradingallowed = isspottradingallowed;
     }
     public boolean getIsspottradingallowed() {
         return isspottradingallowed;
     }

    public void setIsmargintradingallowed(boolean ismargintradingallowed) {
         this.ismargintradingallowed = ismargintradingallowed;
     }
     public boolean getIsmargintradingallowed() {
         return ismargintradingallowed;
     }

    public void setFilters(List<Filters> filters) {
         this.filters = filters;
     }
     public List<Filters> getFilters() {
         return filters;
     }

    public void setPermissions(List<String> permissions) {
         this.permissions = permissions;
     }
     public List<String> getPermissions() {
         return permissions;
     }

    public void setDefaultselftradepreventionmode(String defaultselftradepreventionmode) {
         this.defaultselftradepreventionmode = defaultselftradepreventionmode;
     }
     public String getDefaultselftradepreventionmode() {
         return defaultselftradepreventionmode;
     }

    public void setAllowedselftradepreventionmodes(List<String> allowedselftradepreventionmodes) {
         this.allowedselftradepreventionmodes = allowedselftradepreventionmodes;
     }
     public List<String> getAllowedselftradepreventionmodes() {
         return allowedselftradepreventionmodes;
     }

}