package com.app.bookshop.services;

import com.app.bookshop.dto.Purchase;
import com.app.bookshop.dto.PurchaseResponse;

public interface CheckoutService {

    PurchaseResponse placeOrder(Purchase purchase, String oauthSub, String idempotencyKey);
}
