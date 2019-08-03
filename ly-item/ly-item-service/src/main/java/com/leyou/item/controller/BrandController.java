package com.leyou.item.controller;


import com.leyou.item.service.BrandService;
import com.leyou.item.entity.Brand;
import com.leyou.pojo.BrandDTO;
import com.leyou.item.entity.PageQuery;
import com.leyou.common.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("page")
    public ResponseEntity<PageResult<BrandDTO>> queryBrandForPage(PageQuery pageQuery) {

        //1、调用BrandService方法查询分页数据 将结果返回
        return ResponseEntity.ok(brandService.queryBrandForPage(pageQuery));
    }

    @PostMapping
    public ResponseEntity<Void> insertBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        //1、调用BrandService方法插入数据
        brandService.insert(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        //1、调用BrandService方法插入数据
        brandService.update(brand, cids);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteBrand(@RequestParam("id") Long id) {
        brandService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("of/category")
    public ResponseEntity<List<BrandDTO>> queryBrandByCid(@RequestParam("id") Long cid) {
        return ResponseEntity.ok(this.brandService.queryBrandsByCid(cid));
    }

}
