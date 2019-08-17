package com.leyou.search.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @PostMapping("page")
    public ResponseEntity<PageResult<GoodsDTO>> page(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(goodsService.page(searchRequest));
    }

    @PostMapping("filter")
    public ResponseEntity<Map<String, List<?>>> queryFilter(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(this.goodsService.queryFilter(searchRequest));
    }

}
