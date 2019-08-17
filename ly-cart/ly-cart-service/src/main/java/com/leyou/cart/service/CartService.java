package com.leyou.cart.service;

import com.leyou.cart.entity.Cart;

import java.util.List;

public interface CartService {
    void addCart(Cart cart);

    List<Cart> queryCartsByUid();

    void incrNum(Long id, Integer num);
}
