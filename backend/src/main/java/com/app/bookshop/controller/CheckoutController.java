package com.app.bookshop.controller;

import com.app.bookshop.dto.Purchase;
import com.app.bookshop.dto.PurchaseResponse;
import com.app.bookshop.services.CheckoutService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class CheckoutController {

    private final String API_PATH = "/api/v1";
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping(API_PATH + "/checkout/purchase")
    public PurchaseResponse placeOrder(@Valid @RequestBody Purchase purchase) {
        return checkoutService.placeOrder(purchase);
    }
}
