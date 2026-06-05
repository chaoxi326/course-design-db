package com.example.supermarket.service;

import com.example.supermarket.entity.Supplier;
import java.util.List;

public interface SupplierService {
    // 批量/单条保存
    boolean saveSupplierBatch(List<Supplier> supplierList);
    // 查询全部
    List<Supplier> getAllSuppliers();
    // 修改
    boolean modifySupplier(Supplier supplier);
    // 删除
    boolean removeSupplier(String sId);
}