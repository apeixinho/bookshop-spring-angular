package com.app.catalog.controller;

import java.math.BigDecimal;
import java.util.Map;

import com.app.catalog.currency.CurrencyRates;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyController {

    @GetMapping("/api/v1/currency/rates")
    @Cacheable(value = "currencyRates", key = "'usd'")
    public Map<String, BigDecimal> ratesFromUsd() {
        return CurrencyRates.ratesFromUsd();
    }
}
