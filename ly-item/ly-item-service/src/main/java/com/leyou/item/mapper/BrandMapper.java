package com.leyou.item.mapper;

import com.leyou.item.entity.Brand;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {
    int insertCategoryAndBrand(@Param("bid") Long id, @Param("cids") List<Long> cids);

    int deleteCategoryAndBrandByBid(@Param("bid") Long bid);

    List<Long> queryCategoryIdsByBrandId(@Param("bid")Long bid);

    List<Long> querySkuCountByCategoryIdsAndBrandId(@Param("cids") List<Long> cateGoryIds, @Param("bid") Long bid);

    int deleteCategoryAndBrandByBidAndCids(@Param("cids") List<Long> cateGoryIds, @Param("bid") Long id);

    List<Brand> queryBrandsByCid(@Param("cid") Long cid);
}
