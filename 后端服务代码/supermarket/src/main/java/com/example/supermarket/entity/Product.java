package com.example.supermarket.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Product {
    private String pId;         // 商品编号
    private String pName;       // 商品名称
    private BigDecimal pPrice;  // 商品单价
    private String sId;         // 供应商编号（外键）
    private String pIntro;      // 商品简介
    private String pRemark;     // 备注
}
