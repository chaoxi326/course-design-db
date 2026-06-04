package com.example.supermarket.mapper;

import com.example.supermarket.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SupplierMapper {
    // 1. 批量/单条录入供应商数据
    int insertSupplierBatch(@Param("supplierList") List<Supplier> supplierList);

    // 2. 查询并列出所有供应商信息
    List<Supplier> selectAllSuppliers();

    // 3. 修改指定供应商信息
    int updateSupplier(Supplier supplier);

    // 4. 删除指定供应商数据
    int deleteSupplierById(String sId);
}