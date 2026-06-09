package com.example.supermarket.controller;

import com.example.supermarket.entity.PurchaseOrder;
import com.example.supermarket.entity.PurchaseDetail;
import com.example.supermarket.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    // 💡 用于承载前端同时传过来的主表和明细表数据的临时小口袋
    public static class PurchaseRequest {
        public List<PurchaseOrder> orders;
        public List<PurchaseDetail> details;
    }

    // 1. 录入采购数据 (POST /api/purchase/batch)
    @PostMapping("/batch")
    public ResponseEntity<String> addPurchase(@RequestBody PurchaseRequest request) {
        boolean success = purchaseService.saveOrdersWithDetails(request.orders, request.details);
        return success ? ResponseEntity.ok("采购数据录入成功") : ResponseEntity.badRequest().body("采购数据录入失败");
    }

    // 2. 查询并列出所有采购主表信息 (GET /api/purchase/orders)
    @GetMapping("/orders")
    public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
        return ResponseEntity.ok(purchaseService.getAllOrders());
    }

    // 查询并列出所有采购明细信息 (GET /api/purchase/details)
    @GetMapping("/details")
    public ResponseEntity<List<PurchaseDetail>> getAllDetails() {
        return ResponseEntity.ok(purchaseService.getAllDetails());
    }

    // 3. 修改指定采购主表信息 (PUT /api/purchase/order)
    @PutMapping("/order")
    public ResponseEntity<String> updateOrder(@RequestBody PurchaseOrder order) {
        return purchaseService.modifyOrder(order) ? ResponseEntity.ok("主表修改成功") : ResponseEntity.badRequest().body("主表修改失败");
    }

    // 修改指定采购明细信息 (PUT /api/purchase/detail)
    @PutMapping("/detail")
    public ResponseEntity<String> updateDetail(@RequestBody PurchaseDetail detail) {
        return purchaseService.modifyDetail(detail) ? ResponseEntity.ok("明细修改成功") : ResponseEntity.badRequest().body("明细修改失败");
    }

    // 4. 删除指定采购主表信息和采购明细信息 (DELETE /api/purchase/{oId})
    @DeleteMapping("/{oId}")
    public ResponseEntity<String> deletePurchase(@PathVariable String oId) {
        boolean success = purchaseService.removeOrderWithDetails(oId);
        return success ? ResponseEntity.ok("采购主表及对应明细级联删除成功") : ResponseEntity.badRequest().body("采购数据删除失败");
    }
}