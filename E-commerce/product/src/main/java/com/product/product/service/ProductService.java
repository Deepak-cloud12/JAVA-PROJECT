package com.product.product.service;

import com.product.product.domain.Product;
import com.product.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Method to save a product
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Method to retrieve all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Method to retrieve a product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Method to delete a product by ID
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
}

