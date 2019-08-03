package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.pojo.SkuDTO;
import com.leyou.pojo.SpuDTO;
import com.leyou.pojo.SpuDetailDTO;

import java.util.List;

public interface GoodsService {
    PageResult<SpuDTO> querySpusPage(String key, Boolean saleable, Integer page, Integer rows);

    void insertGoods(SpuDTO spuDTO);

    SpuDetailDTO querySpuDetailBySpuId(Long spuId);

    List<SkuDTO> querySkuBySpuId(Long spuId);

    void updateGoods(SpuDTO spuDTO);

    void updateSaleable(Long spuId, Boolean saleable);
}
