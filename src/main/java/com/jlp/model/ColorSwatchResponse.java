package com.jlp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorSwatchResponse {
    private String color;
    private String rgbColor;
    private String skuId;
}
