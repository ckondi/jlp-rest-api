package com.jlp.service;

import com.jlp.client.ProductDetailsClient;
import com.jlp.entity.PrdDetailsResponse;
import com.jlp.entity.PriceSrc;
import com.jlp.entity.ProductDetails;
import com.jlp.model.Product;
import com.jlp.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ProductDetailsServiceImpl implements ProductDetailsService {

    @Autowired
    private ProductDetailsClient productDetailsClient;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ColorSwatchTransform colorSwatchTransform;

    @Override
    public List<Product> getDresses(String labelType) {

        /*Using Json Parser instead of Feign
        Since one of the price attributes in the product list is not complying with the rest of the price attribute
        contract*/
//        PrdDetailsResponse srcPrdDetails = productDetailsClient.getAllDressesInStock();
        PrdDetailsResponse srcPrdDetails = productRepository.getProductsFromApi();
        List<Product> products = srcPrdDetails.getProducts().stream()
                .filter(Objects::nonNull)
                .filter(isDressOnSale(labelType))
                .map(productDetails -> {
                    Currency currency = Currency.getInstance(productDetails.getPrice().getCurrency());
                    return Product.builder()
                            .productId(productDetails.getProductId())
                            .title(productDetails.getTitle())
                            .colorSwatches(colorSwatchTransform.apply(productDetails.getColorSwatches()))
                            .nowPrice(currency.getSymbol() + formatPrice(productDetails.getPrice().getNow()))
                            .priceLabel(fetchPriceLabel(productDetails.getPrice(), labelType))
                            .build();
                })
                .collect(Collectors.toList());
        return products;
    }

    private Predicate<ProductDetails> isDressOnSale(String labelType) {
        return productDetails -> {
            PriceSrc priceSrc = productDetails.getPrice();
            double now = Double.parseDouble(priceSrc.getNow());
            double was = StringUtils.isEmpty(priceSrc.getWas()) ? 0 : Double.parseDouble(priceSrc.getWas());

            switch (labelType) {
                case LabelType.SHOW_THEN_NOW:
                    double then1 = StringUtils.isEmpty(priceSrc.getThen1()) ? 0 : Double.parseDouble(priceSrc.getThen1());
                    double then2 = StringUtils.isEmpty(priceSrc.getThen2()) ? 0 : Double.parseDouble(priceSrc.getThen2());
                    boolean include = false;
                    if (0 != then2)
                        include = now < then2;
                    else if (0 != then1)
                        include = now < then1;
                    else if (0 != was)
                        include = now < was;
                    return include;
                case LabelType.SHOW_PERCENT_DISCOUNT:
                case LabelType.SHOW_WAS_NOW:
                default:
                    if (0 != was)
                        return now < was;
                    else
                        return false;
            }
        };
    }

    private String fetchPriceLabel(PriceSrc priceSrc, String labelType) {
        Currency currency = Currency.getInstance(priceSrc.getCurrency());
        String symbol = currency.getSymbol();
        switch (labelType) {
            case LabelType.SHOW_PERCENT_DISCOUNT:
                /*Format x% off - now £y.yy*/
                double now = Double.parseDouble(priceSrc.getNow());
                double was = StringUtils.isEmpty(priceSrc.getWas()) ? 0 : Double.parseDouble(priceSrc.getWas());
                double percent = (0 != was) ? ((was - now) / was * 100.0) : 0;
                return MessageFormat.format("{0}% off - now {1}{2}", String.format("%.0f", percent), currency.getSymbol(), formatPrice(priceSrc.getNow()));
            case LabelType.SHOW_THEN_NOW:
                /*Format Was £x.xx, then £y.yy, now £z.zzz*/
                String s1 = MessageFormat.format("Was {0}{1}, ", symbol, formatPrice(priceSrc.getWas()));
                String s2 = null;
                if (!StringUtils.isEmpty(priceSrc.getThen2())) {
                    s2 = MessageFormat.format("then {0}{1}, ", symbol, formatPrice(priceSrc.getThen2()));
                } else if (!StringUtils.isEmpty(priceSrc.getThen1())) {
                    s2 = MessageFormat.format("then {0}{1}, ", symbol, formatPrice(priceSrc.getThen1()));
                }
                String s3 = MessageFormat.format("now {0}{1}", symbol, formatPrice(priceSrc.getNow()));
                return StringUtils.isEmpty(s2) ? (s1 + s3) : (s1 + s2 + s3);
            case LabelType.SHOW_WAS_NOW:
            default:
                /*Was £x.xx, now £y.yyy*/
                return MessageFormat.format("Was {0}{1} , now {2}{3}", symbol, formatPrice(priceSrc.getWas()), symbol, formatPrice(priceSrc.getNow()));
        }
    }

    private String formatPrice(String price) {
        double nowPrice = Double.parseDouble(price);
        if (nowPrice > 10) {
            return String.valueOf((int) nowPrice);
        } else {
            return String.format("%.2f", nowPrice);
        }
    }
}
