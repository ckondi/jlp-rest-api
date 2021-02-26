package com.jlp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String productId;
    private String title;
    private List<ColorSwatchResponse> colorSwatches;
    private String nowPrice;
    private String priceLabel;
}
