package com.example.supermarket.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PurchaseDetail {
    @JsonProperty("dId") private String dId;
    @JsonProperty("oId") private String oId;
    @JsonProperty("pId") private String pId;
    @JsonProperty("dQuantity") private Integer dQuantity;
    @JsonProperty("dPrice") private BigDecimal dPrice;
    @JsonProperty("dTotalPrice") private BigDecimal dTotalPrice;
    @JsonProperty("dRemark") private String dRemark;
}
