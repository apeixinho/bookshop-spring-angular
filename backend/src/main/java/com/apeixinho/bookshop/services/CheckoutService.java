package com.apeixinho.bookshop.services;

import com.apeixinho.bookshop.dto.Purchase;
import com.apeixinho.bookshop.dto.PurchaseResponse;

public interface CheckoutService {

    PurchaseResponse placeOrder(Purchase purchase);
}
