package com.example.supermarket.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseOrder {
    private String oId;             // 采购清单号
    private String eId;             // 员工编号（外键）
    private Integer oTotalQuantity; // 采购数量
    private BigDecimal oTotalPrice; // 采购总价
    private LocalDateTime oTime;    // 采购时间
    private String oRemark;         // 备注
}