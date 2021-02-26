package com.jlp.config;

import com.jlp.exception.BadRequestException;
import com.jlp.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class ProductSvcErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {

            switch (response.status()){
                case 400:
                    return new BadRequestException("Product Service Response: " + response.body());
                case 404:
                    return new NotFoundException("Product Service URI not found");
                default:
                    return new Exception("System Error");
            }
        }
}
