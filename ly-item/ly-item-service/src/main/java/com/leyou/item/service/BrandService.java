package com.leyou.item.service;

import com.leyou.pojo.Brand;
import com.leyou.pojo.BrandDTO;
import com.leyou.pojo.PageQuery;
import com.leyou.pojo.PageResult;

import java.util.List;

public interface BrandService {
    PageResult<BrandDTO> queryBrandForPage(PageQuery pageQuery);

    void insert(Brand brand, List<Long> cids);
}
