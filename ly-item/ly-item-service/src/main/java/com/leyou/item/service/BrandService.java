package com.leyou.item.service;

import com.leyou.item.entity.Brand;
import com.leyou.pojo.BrandDTO;
import com.leyou.item.entity.PageQuery;
import com.leyou.common.pojo.PageResult;

import java.util.List;

public interface BrandService {
    PageResult<BrandDTO> queryBrandForPage(PageQuery pageQuery);

    void insert(Brand brand, List<Long> cids);

    void update(Brand brand, List<Long> cids);

    void deleteById(Long id);

    BrandDTO queryBrandNameByBid(Long brandId);

    List<BrandDTO> queryBrandsByCid(Long cid);
}
