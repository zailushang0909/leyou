package com.leyou.item.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.entity.Category;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.service.CategoryService;
import com.leyou.pojo.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getCategoryByParentId(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List<Category> categories = categoryMapper.select(category);
        if (CollectionUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_EXIST);
        }
        List<CategoryDTO> categoryDTOS = BeanHelper.copyWithCollection(categories, CategoryDTO.class);
        return categoryDTOS;
    }

    @Override
    public List<CategoryDTO> getCategoryById(Long id) {
        //1、根据品牌id查询分类DTO
        List<Category> categories = categoryMapper.selectCategorysByBrandId(id);
        if (CollectionUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        return BeanHelper.copyWithCollection(categories,CategoryDTO.class);
    }

    @Override
    public List<CategoryDTO> getCategorysByCids(List<Long> cids) {
        List<Category> categories = categoryMapper.selectByIdList(cids);
        if (CollectionUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        return BeanHelper.copyWithCollection(categories,CategoryDTO.class);
    }

}
