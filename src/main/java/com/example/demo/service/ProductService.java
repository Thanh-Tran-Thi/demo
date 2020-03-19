package com.example.demo.service;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Product createProducts (Product product);

    List<Product> listProducts();
}
