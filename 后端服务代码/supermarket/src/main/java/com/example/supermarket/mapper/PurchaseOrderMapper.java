package com.example.supermarket.mapper;

import com.example.supermarket.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PurchaseOrderMapper {
    int insertOrderBatch(@Param("orderList") List<PurchaseOrder> orderList);
    List<PurchaseOrder> selectAllOrders();
    int updateOrder(PurchaseOrder order);
    int deleteOrderById(String oId);
}