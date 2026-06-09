package com.example.supermarket.controller;

import com.example.supermarket.common.Result;
import com.example.supermarket.entity.PurchaseOrder;
import com.example.supermarket.entity.PurchaseDetail;
import com.example.supermarket.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    public static class PurchaseRequest {
        public List<PurchaseOrder> orders;
        public List<PurchaseDetail> details;
    }

    @PostMapping("/batch")
    public Result<Void> addPurchase(@RequestBody PurchaseRequest request) {
        return purchaseService.saveOrdersWithDetails(request.orders, request.details)
                ? Result.success()
                : Result.fail("采购数据录入失败");
    }

    @GetMapping("/orders")
    public Result<List<PurchaseOrder>> getAllOrders() {
        return Result.success(purchaseService.getAllOrders());
    }

    @GetMapping("/details")
    public Result<List<PurchaseDetail>> getAllDetails() {
        return Result.success(purchaseService.getAllDetails());
    }

    @PutMapping("/order")
    public Result<Void> updateOrder(@RequestBody PurchaseOrder order) {
        return purchaseService.modifyOrder(order)
                ? Result.success()
                : Result.fail("主表修改失败");
    }

    @PutMapping("/detail")
    public Result<Void> updateDetail(@RequestBody PurchaseDetail detail) {
        return purchaseService.modifyDetail(detail)
                ? Result.success()
                : Result.fail("明细修改失败");
    }

    @DeleteMapping("/{oId}")
    public Result<Void> deletePurchase(@PathVariable String oId) {
        return purchaseService.removeOrderWithDetails(oId)
                ? Result.success()
                : Result.fail("采购数据删除失败");
    }
}
