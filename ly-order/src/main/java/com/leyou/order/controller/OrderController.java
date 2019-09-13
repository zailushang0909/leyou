package com.leyou.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OrderController {

//    @Autowired
//    private OrderService orderService;

    @PostMapping("order")
    private ResponseEntity<Void> order(@RequestBody Map order) {
//        orderService.order();
        return null;
    }

}
