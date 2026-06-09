package com.example.supermarket.controller;

import com.example.supermarket.common.Result;
import com.example.supermarket.entity.Supplier;
import com.example.supermarket.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping("/batch")
    public Result<Void> addSupplierBatch(@RequestBody List<Supplier> supplierList) {
        return supplierService.saveSupplierBatch(supplierList)
                ? Result.success()
                : Result.fail("供应商录入失败");
    }

    @GetMapping
    public Result<List<Supplier>> getAllSuppliers() {
        return Result.success(supplierService.getAllSuppliers());
    }

    @PutMapping
    public Result<Void> updateSupplier(@RequestBody Supplier supplier) {
        return supplierService.modifySupplier(supplier)
                ? Result.success()
                : Result.fail("供应商信息修改失败");
    }

    @DeleteMapping("/{sId}")
    public Result<Void> deleteSupplier(@PathVariable String sId) {
        return supplierService.removeSupplier(sId)
                ? Result.success()
                : Result.fail("供应商删除失败");
    }
}
