package com.jlp.client;

import com.jlp.entity.PrdDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "${app.product.service.name}",
        url = "${app.product.service.url}")
public interface ProductDetailsClient {

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    PrdDetailsResponse getAllDressesInStock();
}
