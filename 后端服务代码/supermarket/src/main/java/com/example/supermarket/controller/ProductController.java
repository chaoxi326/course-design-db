package com.example.supermarket.controller;

import com.example.supermarket.entity.Product;
import com.example.supermarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/batch")
    public ResponseEntity<String> addProductBatch(@RequestBody List<Product> productList) {
        boolean success = productService.saveProductBatch(productList);
        return success ? ResponseEntity.ok("商品录入成功") : ResponseEntity.badRequest().body("商品录入失败");
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping
    public ResponseEntity<String> updateProduct(@RequestBody Product product) {
        return productService.modifyProduct(product) ? ResponseEntity.ok("修改成功") : ResponseEntity.badRequest().body("修改失败");
    }

    @DeleteMapping("/{pId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String pId) {
        return productService.removeProduct(pId) ? ResponseEntity.ok("删除成功") : ResponseEntity.badRequest().body("删除失败");
    }
}