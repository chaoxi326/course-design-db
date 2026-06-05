package com.example.supermarket.service.impl;

import com.example.supermarket.entity.PurchaseOrder;
import com.example.supermarket.entity.PurchaseDetail;
import com.example.supermarket.mapper.PurchaseOrderMapper;
import com.example.supermarket.mapper.PurchaseDetailMapper;
import com.example.supermarket.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseOrderMapper orderMapper;
    private final PurchaseDetailMapper detailMapper;

    @Override
    @Transactional // 💡 开启事务：主表和明细表的插入要么同时成功，要么同时失败
    public boolean saveOrdersWithDetails(List<PurchaseOrder> orders, List<PurchaseDetail> details) {
        boolean orderSuccess = true;
        boolean detailSuccess = true;

        if (orders != null && !orders.isEmpty()) {
            orderSuccess = orderMapper.insertOrderBatch(orders) > 0;
        }
        if (details != null && !details.isEmpty()) {
            detailSuccess = detailMapper.insertDetailBatch(details) > 0;
        }
        return orderSuccess && detailSuccess;
    }

    @Override
    public List<PurchaseOrder> getAllOrders() {
        return orderMapper.selectAllOrders(); // 对应你主表 Mapper 的查询方法
    }

    @Override
    public List<PurchaseDetail> getAllDetails() {
        return detailMapper.selectAllDetails(); // 对应你明细表 Mapper 的查询方法
    }

    @Override
    public boolean modifyOrder(PurchaseOrder order) {
        return orderMapper.updateOrder(order) > 0;
    }

    @Override
    public boolean modifyDetail(PurchaseDetail detail) {
        return detailMapper.updateDetail(detail) > 0;
    }

    @Override
    @Transactional // 💡 开启事务：删除时必须先删外键明细，再删主表，错一个都不行
    public boolean removeOrderWithDetails(String oId) {
        // 1. 先调用明细表的 Mapper 删掉当前订单下的所有商品明细
        detailMapper.deleteDetailByOrderId(oId);
        // 2. 再调用主表的 Mapper 删掉订单主记录
        return orderMapper.deleteOrderById(oId) > 0;
    }
}