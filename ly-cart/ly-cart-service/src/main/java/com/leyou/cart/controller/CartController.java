package com.leyou.cart.controller;

import com.leyou.cart.entity.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("cart")
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        this.cartService.addCart(cart);
        return ResponseEntity.ok().build();
    }

    @GetMapping("cart/list")
    public ResponseEntity<List<Cart>> queryCartsByUid() {

        return ResponseEntity.ok(this.cartService.queryCartsByUid());
    }

    @PutMapping("cart")
    public ResponseEntity<Void> incrNum(@RequestParam("id") Long id,@RequestParam("num") Integer num) {
        this.cartService.incrNum(id,num);
        return ResponseEntity.ok().build();
    }
}
