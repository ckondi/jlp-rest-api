package com.jlp.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlp.entity.PrdDetailsResponse;
import com.jlp.entity.ProductDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class ProductRepository {

    private final String dressesUrl;

    public ProductRepository(@Value("${app.product.service.url}") String dressesUrl) {
        this.dressesUrl = dressesUrl;
    }

    public PrdDetailsResponse getProductsFromApi() {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(dressesUrl, String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode products = root.path("products");
            if (products.isArray()) {
                List<JsonNode> jsonNodeList = StreamSupport.stream(products.spliterator(), false).collect(Collectors.toList());
                List<ProductDetails> productDetailsList = jsonNodeList.stream()
                        .map(prd -> {
                            try {
                                return mapper.treeToValue(prd, ProductDetails.class);
                            } catch (JsonProcessingException e) {
                                log.error("Not able to parse :{}", prd, e);
                            }
                            return null;
                        })
                        .collect(Collectors.toList());
                return PrdDetailsResponse.builder().products(productDetailsList).build();
            }
        } catch (JsonProcessingException e) {
            log.error("Not able to parse Json Response");
        }
        return PrdDetailsResponse.builder().build();
    }
}
