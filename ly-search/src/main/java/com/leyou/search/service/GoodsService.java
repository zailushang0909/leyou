package com.leyou.search.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.pojo.SpuDTO;

import java.util.List;
import java.util.Map;

public interface GoodsService {
    Goods buildGoods(Long spuId);

    PageResult<GoodsDTO> page(SearchRequest searchRequest);

    Map<String, List<?>> queryFilter(SearchRequest key);

    void insertDoc(Long sid);

    void deleteById(Long sid);
}
