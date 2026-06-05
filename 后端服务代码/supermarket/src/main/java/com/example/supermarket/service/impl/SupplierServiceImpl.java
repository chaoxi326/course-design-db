package com.example.supermarket.service.impl;

import com.example.supermarket.entity.Supplier;
import com.example.supermarket.mapper.SupplierMapper;
import com.example.supermarket.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // 💡 别漏了：告诉Spring这是一个业务层组件
public class SupplierServiceImpl implements SupplierService {

    @Autowired // 💡 自动把你的仓库管理员（Mapper）拉进来用
    private SupplierMapper supplierMapper;

    @Override
    public boolean saveSupplierBatch(List<Supplier> supplierList) {
        if (supplierList == null || supplierList.isEmpty()) return false;
        // 调用你在 Mapper 接口里定义的批量插入
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