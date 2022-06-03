package com.caicongyang.local;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreAvailableStockNumDTO implements Serializable {

    /**
     * 店铺Id
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺编码
     */
    private String storeCode;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 店铺商品ID
     */
    private Long itemId;

    /**
     * 虚拟库存的可用数量。 非业务填写，系统自动计算的=实际-冻结
     */
    private BigDecimal virtualAvailableStockNum;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 如果是虚品的话包含子品信息
     */
    private List<StoreAvailableStockNumDTO> storeAvailableStockNumOutDTOS;

    /**
     * 产品ID
     */
    private Long productId;

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getVirtualAvailableStockNum() {
        return virtualAvailableStockNum;
    }

    public void setVirtualAvailableStockNum(BigDecimal virtualAvailableStockNum) {
        this.virtualAvailableStockNum = virtualAvailableStockNum;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public List<StoreAvailableStockNumDTO> getStoreAvailableStockNumOutDTOS() {
        return storeAvailableStockNumOutDTOS;
    }

    public void setStoreAvailableStockNumOutDTOS(List<StoreAvailableStockNumDTO> storeAvailableStockNumOutDTOS) {
        this.storeAvailableStockNumOutDTOS = storeAvailableStockNumOutDTOS;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
