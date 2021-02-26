package com.jlp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColorSwatch {
    private String color;
    private String basicColor;
    private String colorSwatchUrl;
    private String imageUrl;
    private Boolean isAvailable;
    private String skuId;
}
