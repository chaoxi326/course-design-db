package com.example.supermarket.mapper;

import com.example.supermarket.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    // 1. 批量录入商品数据（单条也是传一个元素的集合）
    int insertProductBatch(@Param("productList") List<Product> productList);

    // 2. 查询并列出所有商品信息
    List<Product> selectAllProducts();

    // 3. 修改指定商品的商品信息
    int updateProduct(Product product);

    // 4. 删除指定商品数据
    int deleteProductById(String pId);
}