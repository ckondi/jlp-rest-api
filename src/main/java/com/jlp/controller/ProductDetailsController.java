package com.jlp.controller;

import com.jlp.model.DressesResponse;
import com.jlp.model.Product;
import com.jlp.service.ProductDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/v1/product/sale", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ProductDetailsController {
    @Autowired
    private ProductDetailsService productDetailsService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public DressesResponse doGet(
            /* AllowableValues for priceLabel Param - {"ShowWasNow", "ShowWasThenNow", "ShowPercDiscount"}*/
            @RequestParam(value = "priceLabel", required = false, defaultValue = "ShowWasNow") String priceLabel) {
        List<Product> productList = productDetailsService.getDresses(priceLabel);
        return DressesResponse.builder().products(productList).build();
    }
}
