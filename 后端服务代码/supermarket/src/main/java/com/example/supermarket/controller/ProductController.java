package com.example.supermarket.controller;

import com.example.supermarket.common.Result;
import com.example.supermarket.entity.Product;
import com.example.supermarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/batch")
    public Result<Void> addProductBatch(@RequestBody List<Product> productList) {
        return productService.saveProductBatch(productList)
                ? Result.success()
                : Result.fail("商品录入失败");
    }

    @GetMapping
    public Result<List<Product>> getAllProducts() {
        return Result.success(productService.getAllProducts());
    }

    @PutMapping
    public Result<Void> updateProduct(@RequestBody Product product) {
        return productService.modifyProduct(product)
                ? Result.success()
                : Result.fail("修改失败");
    }

    @DeleteMapping("/{pId}")
    public Result<Void> deleteProduct(@PathVariable String pId) {
        return productService.removeProduct(pId)
                ? Result.success()
                : Result.fail("删除失败");
    }
}
