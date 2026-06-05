package com.example.supermarket.service.impl;

import com.example.supermarket.entity.Product;
import com.example.supermarket.mapper.ProductMapper;
import com.example.supermarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public boolean saveProductBatch(List<Product> productList) {
        if (productList == null || productList.isEmpty()) return false;
        // 假设你在 ProductMapper 里写的方法叫 insertProductBatch
        return productMapper.insertProductBatch(productList) > 0;
    }

    @Override
    public List<Product> getAllProducts() {
        return productMapper.selectAllProducts(); // 对应你的 Mapper 查询方法
    }

    @Override
    public boolean modifyProduct(Product product) {
        return productMapper.updateProduct(product) > 0;
    }

    @Override
    public boolean removeProduct(String pId) {
        return productMapper.deleteProductById(pId) > 0;
    }
}