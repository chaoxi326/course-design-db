package com.example.supermarket.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseOrder {
    @JsonProperty("oId") private String oId;             // 采购清单号
    @JsonProperty("eId") private String eId;             // 员工编号（外键）
    @JsonProperty("oTotalQuantity") private Integer oTotalQuantity; // 采购数量
    @JsonProperty("oTotalPrice") private BigDecimal oTotalPrice; // 采购总价
    @JsonProperty("oTime") private LocalDateTime oTime;    // 采购时间
    @JsonProperty("oRemark") private String oRemark;         // 备注
}
