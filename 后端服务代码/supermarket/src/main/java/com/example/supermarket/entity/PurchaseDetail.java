package com.example.supermarket.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseDetail {
    private  String dId;
    private  String oId;
    private  String pId;
    private  Integer dQuantity;
    private  BigDecimal dPrice;
    private  BigDecimal dTotalPrice;
    private  String dRemark;
}
