package com.apeixinho.bookshop.dto;

import java.util.Set;

import com.apeixinho.bookshop.entity.Address;
import com.apeixinho.bookshop.entity.Customer;
import com.apeixinho.bookshop.entity.Order;
import com.apeixinho.bookshop.entity.OrderItem;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Purchase {

    @NotNull
    @Valid
    private Customer customer;

    @NotNull
    @Valid
    private Address shippingAddress;

    @NotNull
    @Valid
    private Address billingAddress;

    @NotNull
    @Valid
    private Order order;

    @NotNull
    @NotEmpty
    @Valid
    private Set<OrderItem> orderItems;
}
