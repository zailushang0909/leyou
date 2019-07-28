package com.leyou.item.service;

import com.leyou.pojo.CategoryDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getCategoryByParentId(Long pid);
}
