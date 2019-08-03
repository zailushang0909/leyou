package com.leyou.item.service;

import com.leyou.item.entity.Category;
import com.leyou.pojo.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getCategoryByParentId(Long pid);

    List<CategoryDTO> getCategoryById(Long id);

    List<CategoryDTO> getCategorysNameByCids(List<Long> cids);
}
