package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.service.GoodsService;
import com.leyou.pojo.SkuDTO;
import com.leyou.pojo.SpuDTO;
import com.leyou.pojo.SpuDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuDTO>> querySpusPage(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable" ,required = false) Boolean saleable,
            @RequestParam(value="page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "10") Integer rows) {

        return ResponseEntity.ok(this.goodsService.querySpusPage(key,saleable,page,rows));
    }

    @PostMapping("/goods")
    public ResponseEntity<Void> insertGoods(@RequestBody SpuDTO spuDTO) {
        goodsService.insertGoods(spuDTO);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("spu/detail")
    public ResponseEntity<SpuDetailDTO> querySpuDetailBySpuId(@RequestParam("id") Long spuId) {
        return ResponseEntity.ok(goodsService.querySpuDetailBySpuId(spuId));
    }

    @GetMapping("sku/of/spu")
    public ResponseEntity<List<SkuDTO>> querySkuBySpuId(@RequestParam("id") Long spuId) {
        return ResponseEntity.ok(goodsService.querySkuBySpuId(spuId));
    }

    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO) {
        goodsService.updateGoods(spuDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("spu/saleable")
    public ResponseEntity<Void> updateSaleable(@RequestParam("id") Long spuId, @RequestParam("saleable") Boolean saleable) {
        goodsService.updateSaleable(spuId, saleable);
        return ResponseEntity.ok().build();
    }

    @GetMapping("spu/of/{spuId}")
    public ResponseEntity<SpuDTO> querySpuById(@PathVariable("spuId") Long spuId) {
        return ResponseEntity.ok(goodsService.querySpuById(spuId));
    }

    @GetMapping("/sku/list")
    public ResponseEntity<List<SkuDTO>> querySkusByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(this.goodsService.querySkusByIds(ids));
    }

}
