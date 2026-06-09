package com.example.supermarket.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Supplier {
    @JsonProperty("sId") private String sId;           // 供应商编号
    @JsonProperty("sName") private String sName;         // 供应商名称
    @JsonProperty("sShortName") private String sShortName;    // 供应商简称
    @JsonProperty("sAddress") private String sAddress;      // 地址
    @JsonProperty("sPhone") private String sPhone;        // 公司电话
    @JsonProperty("sEmail") private String sEmail;        // 邮件
    @JsonProperty("sContactPerson") private String sContactPerson;// 联系人
    @JsonProperty("sContactPhone") private String sContactPhone; // 联系人电话
    @JsonProperty("sRemark") private String sRemark;
}
