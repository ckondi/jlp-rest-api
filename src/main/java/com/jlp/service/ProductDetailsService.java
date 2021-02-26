package com.jlp.service;

import com.jlp.model.Product;

import java.util.List;

public interface ProductDetailsService {
    List<Product> getDresses(String labelType);
}
