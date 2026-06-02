package com.example.supermarket.entity;

import lombok.Data;

@Data
public class Supplier {
    private String sId;           // 供应商编号
    private String sName;         // 供应商名称
    private String sShortName;    // 供应商简称
    private String sAddress;      // 地址
    private String sPhone;        // 公司电话
    private String sEmail;        // 邮件
    private String sContactPerson;// 联系人
    private String sContactPhone; // 联系人电话
    private String sRemark;
}
