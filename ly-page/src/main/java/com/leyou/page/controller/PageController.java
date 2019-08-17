package com.leyou.page.controller;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("item/{sid}.html")
    public String getSpuHtmlById(@PathVariable("sid") Long sid, Model model) {
        Map<String,Object> spuData = pageService.loadData(sid);
        model.addAllAttributes(spuData);
        return "item";
    }

}
