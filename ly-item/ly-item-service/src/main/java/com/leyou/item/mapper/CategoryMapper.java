package com.leyou.item.mapper;

import com.leyou.item.entity.Category;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<Category> , SelectByIdListMapper<Category,Long> {
    List<Category> selectCategorysByBrandId(@Param("bid") Long bid);
}
