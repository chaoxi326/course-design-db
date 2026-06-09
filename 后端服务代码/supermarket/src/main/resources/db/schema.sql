-- ============================================================
-- 超市进销存管理系统 — 数据库建表脚本
-- 数据库: supermarket_db
-- 字符集: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS supermarket_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE supermarket_db;

-- 1. 供应商表
DROP TABLE IF EXISTS purchase_detail;
DROP TABLE IF EXISTS purchase_order;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS supplier;

CREATE TABLE supplier
(
    s_id            VARCHAR(20)  NOT NULL COMMENT '供应商编号',
    s_name          VARCHAR(100) NOT NULL COMMENT '供应商名称',
    s_short_name    VARCHAR(50)  DEFAULT NULL COMMENT '供应商简称',
    s_address       VARCHAR(200) DEFAULT NULL COMMENT '地址',
    s_phone         VARCHAR(20)  DEFAULT NULL COMMENT '公司电话',
    s_email         VARCHAR(100) DEFAULT NULL COMMENT '邮件',
    s_contact_person VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    s_contact_phone VARCHAR(20)  DEFAULT NULL COMMENT '联系人电话',
    s_remark        VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (s_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='供应商表';

-- 2. 商品表
CREATE TABLE product
(
    p_id     VARCHAR(20)    NOT NULL COMMENT '商品编号',
    p_name   VARCHAR(100)   NOT NULL COMMENT '商品名称',
    p_price  DECIMAL(10, 2) NOT NULL COMMENT '商品单价',
    s_id     VARCHAR(20)    NOT NULL COMMENT '供应商编号（外键）',
    p_intro  VARCHAR(500)   DEFAULT NULL COMMENT '商品简介',
    p_remark VARCHAR(500)   DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (p_id),
    CONSTRAINT fk_product_supplier FOREIGN KEY (s_id) REFERENCES supplier (s_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='商品表';

-- 3. 员工表
CREATE TABLE employee
(
    e_id       VARCHAR(20)    NOT NULL COMMENT '员工编号',
    e_name     VARCHAR(50)    NOT NULL COMMENT '员工姓名',
    e_password VARCHAR(255)   NOT NULL COMMENT '密码',
    e_level    VARCHAR(20)    DEFAULT NULL COMMENT '级别',
    e_phone    VARCHAR(20)    DEFAULT NULL COMMENT '电话',
    e_salary   DECIMAL(10, 2) DEFAULT NULL COMMENT '工资',
    e_remark   VARCHAR(500)   DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (e_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='员工表';

-- 4. 采购订单主表
CREATE TABLE purchase_order
(
    o_id             VARCHAR(20)    NOT NULL COMMENT '采购清单号',
    e_id             VARCHAR(20)    NOT NULL COMMENT '员工编号（外键）',
    o_total_quantity INT            NOT NULL COMMENT '采购总数量',
    o_total_price    DECIMAL(12, 2) NOT NULL COMMENT '采购总价',
    o_time           DATETIME       DEFAULT NULL COMMENT '采购时间',
    o_remark         VARCHAR(500)   DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (o_id),
    CONSTRAINT fk_order_employee FOREIGN KEY (e_id) REFERENCES employee (e_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='采购订单主表';

-- 5. 采购明细表
CREATE TABLE purchase_detail
(
    d_id         VARCHAR(20)    NOT NULL COMMENT '明细编号',
    o_id         VARCHAR(20)    NOT NULL COMMENT '订单编号（外键）',
    p_id         VARCHAR(20)    NOT NULL COMMENT '商品编号（外键）',
    d_quantity   INT            NOT NULL COMMENT '采购数量',
    d_price      DECIMAL(10, 2) NOT NULL COMMENT '采购单价',
    d_total_price DECIMAL(12, 2) NOT NULL COMMENT '采购小计',
    d_remark     VARCHAR(500)   DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (d_id),
    CONSTRAINT fk_detail_order FOREIGN KEY (o_id) REFERENCES purchase_order (o_id) ON DELETE CASCADE,
    CONSTRAINT fk_detail_product FOREIGN KEY (p_id) REFERENCES product (p_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='采购明细表';
