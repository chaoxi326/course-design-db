package com.example.supermarket.service;

import com.example.supermarket.entity.PurchaseOrder;
import com.example.supermarket.entity.PurchaseDetail;
import java.util.List;

public interface PurchaseService {
    // 1. 级联保存订单主表和明细表
    boolean saveOrdersWithDetails(List<PurchaseOrder> orders, List<PurchaseDetail> details);

    // 2. 查询所有的主表和明细表数据
    List<PurchaseOrder> getAllOrders();
    List<PurchaseDetail> getAllDetails();

    // 3. 修改
    boolean modifyOrder(PurchaseOrder order);
    boolean modifyDetail(PurchaseDetail detail);

    // 4. 级联删除（严格对应需求：同时删除主表和明细）
    boolean removeOrderWithDetails(String oId);
}