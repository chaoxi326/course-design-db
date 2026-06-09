package com.example.supermarket.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class Employee {
    @JsonProperty("eId") private String eId;         // 员工编号
    @JsonProperty("eName") private String eName;       // 员工姓名
    @JsonProperty("ePassword") private String ePassword;   // 密码
    @JsonProperty("eLevel") private String eLevel;      // 级别
    @JsonProperty("ePhone") private String ePhone;      // 电话
    @JsonProperty("eSalary") private BigDecimal eSalary; // 工资
    @JsonProperty("eRemark") private String eRemark;     // 备注
}
