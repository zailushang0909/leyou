package com.leyou.item.controller;

import com.leyou.pojo.CategoryDTO;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> getCategoryByParentId(@RequestParam("pid") Long pid) {
        List<CategoryDTO> categoryDTOS = categoryService.getCategoryByParentId(pid);
        return ResponseEntity.status(200).body(categoryDTOS);
    }

}