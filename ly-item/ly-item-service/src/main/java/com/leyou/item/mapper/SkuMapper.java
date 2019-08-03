package com.leyou.item.mapper;

import com.leyou.item.entity.Sku;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku>, InsertListMapper<Sku> , DeleteByIdListMapper<Sku,Long> {
    int updateEnableBySpuId(@Param("spuId") Long spuId, @Param("enable") Boolean enable);
}
