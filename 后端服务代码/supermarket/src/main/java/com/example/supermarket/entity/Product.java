package com.example.supermarket.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class Product {
    @JsonProperty("pId") private String pId;         // 商品编号
    @JsonProperty("pName") private String pName;       // 商品名称
    @JsonProperty("pPrice") private BigDecimal pPrice;  // 商品单价
    @JsonProperty("sId") private String sId;         // 供应商编号（外键）
    @JsonProperty("pIntro") private String pIntro;      // 商品简介
    @JsonProperty("pRemark") private String pRemark;     // 备注
}
