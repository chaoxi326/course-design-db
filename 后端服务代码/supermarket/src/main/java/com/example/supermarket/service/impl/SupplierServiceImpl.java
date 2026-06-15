package com.example.supermarket.service.impl;

import com.example.supermarket.entity.Supplier;
import com.example.supermarket.mapper.SupplierMapper;
import com.example.supermarket.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierMapper supplierMapper;

    @Override
    public boolean saveSupplierBatch(List<Supplier> supplierList) {
        if (supplierList == null || supplierList.isEmpty()) return false;
        for (Supplier s : supplierList) {
            if (s.getSId() == null || s.getSId().isBlank()) {
                throw new IllegalArgumentException("供应商编号不能为空");
            }
            if (s.getSName() == null || s.getSName().isBlank()) {
                throw new IllegalArgumentException("供应商名称不能为空");
            }
        }
        return supplierMapper.insertSupplierBatch(supplierList) > 0;
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierMapper.selectAllSuppliers();
    }

    @Override
    public boolean modifySupplier(Supplier supplier) {
        return supplierMapper.updateSupplier(supplier) > 0;
    }

    @Override
    public boolean removeSupplier(String sId) {
        return supplierMapper.deleteSupplierById(sId) > 0;
    }
}