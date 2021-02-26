package com.jlp.service;

import com.jlp.entity.ColorSwatch;
import com.jlp.model.ColorSwatchResponse;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ColorSwatchTransform implements Function<List<ColorSwatch>, List<ColorSwatchResponse>> {

    private Map<String, Integer> basicColorMap;

    @PostConstruct
    public void loadColorMap() {
        basicColorMap = new HashMap<String, Integer>();
        for (Field f : Color.class.getFields()) {
            if (f.getType() == Color.class) {
                Color c = null;
                try {
                    c = (Color) f.get(null);
                } catch (IllegalAccessException e) {
                    /*Not expecting an exception*/
                }
                basicColorMap.put(f.getName().toUpperCase(), c.getRGB());
            }
        }
    }

    @Override
    public List<ColorSwatchResponse> apply(List<ColorSwatch> colorSwatches) {

        return colorSwatches.stream()
                .map(colorSwatch -> {
                    Integer resultRGB = basicColorMap.getOrDefault(colorSwatch.getBasicColor().toUpperCase(), Color.WHITE.getRGB());
                    return ColorSwatchResponse.builder()
                            .color(colorSwatch.getColor())
                            .skuId(colorSwatch.getSkuId())
                            .rgbColor(Integer.toHexString(resultRGB).toUpperCase())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
