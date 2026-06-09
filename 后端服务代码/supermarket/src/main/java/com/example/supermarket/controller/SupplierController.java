package com.example.supermarket.controller;

import com.example.supermarket.entity.Supplier;
import com.example.supermarket.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 💡 标志这是一个前后端分离的控制层接口类
@RequestMapping("/api/suppliers") // 💡 前端访问这个模块的基本网址
public class SupplierController {

    @Autowired // 💡 把刚刚写好的大厨（Service）引入进来
    private SupplierService supplierService;

    // 录入（单条或批量，前端都传 JSON 数组格式）
    @PostMapping("/batch")
    public ResponseEntity<String> addSupplierBatch(@RequestBody List<Supplier> supplierList) {
        boolean success = supplierService.saveSupplierBatch(supplierList);
        return success ? ResponseEntity.ok("供应商录入成功") : ResponseEntity.badRequest().body("供应商录入失败");
    }

    // 查询所有供应商
    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    // 修改指定供应商
    @PutMapping
    public ResponseEntity<String> updateSupplier(@RequestBody Supplier supplier) {
        boolean success = supplierService.modifySupplier(supplier);
        return success ? ResponseEntity.ok("供应商信息修改成功") : ResponseEntity.badRequest().body("供应商信息修改失败");
    }

    // 删除指定供应商
    @DeleteMapping("/{sId}")
    public ResponseEntity<String> deleteSupplier(@PathVariable String sId) {
        boolean success = supplierService.removeSupplier(sId);
        return success ? ResponseEntity.ok("供应商删除成功") : ResponseEntity.badRequest().body("供应商删除失败");
    }
}