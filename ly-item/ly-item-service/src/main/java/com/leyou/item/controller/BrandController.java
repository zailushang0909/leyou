package com.leyou.item.controller;


import com.leyou.item.service.BrandService;
import com.leyou.pojo.Brand;
import com.leyou.pojo.BrandDTO;
import com.leyou.pojo.PageQuery;
import com.leyou.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("insert")
    public ResponseEntity<Void> insertBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        //1、调用BrandService方法插入数据
        brandService.insert(brand, cids);
        return ResponseEntity.ok().build();
    }

}
