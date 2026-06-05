package com.example.supermarket.service;

import com.example.supermarket.entity.Product;
import java.util.List;

public interface ProductService {
    boolean saveProductBatch(List<Product> productList);
    List<Product> getAllProducts();
    boolean modifyProduct(Product product);
    boolean removeProduct(String pId);
}