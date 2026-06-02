package com.example.supermarket.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class  Employee {
    private String eId;         // 员工编号
    private String eName;       // 员工姓名
    private String ePassword;   // 密码
    private String eLevel;      // 级别
    private String ePhone;      // 电话
    private BigDecimal eSalary; // 工资
    private String eRemark;     // 备注
}
