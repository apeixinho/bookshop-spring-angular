package com.apeixinho.bookshop.controller;

import com.apeixinho.bookshop.dto.Purchase;
import com.apeixinho.bookshop.dto.PurchaseResponse;
import com.apeixinho.bookshop.services.CheckoutService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class CheckoutController {

    private final String API_PATH = "/api/v1";

    private final CheckoutService checkoutService;

    @PostMapping(API_PATH + "/checkout/purchase")
    public PurchaseResponse placeOrder(@Valid @RequestBody Purchase purchase) {
        return checkoutService.placeOrder(purchase);
    }
}
