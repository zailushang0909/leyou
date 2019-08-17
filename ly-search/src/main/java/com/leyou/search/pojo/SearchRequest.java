package com.leyou.search.pojo;

import java.util.Map;

public class SearchRequest {
    private String key;// 搜索条件

    private Integer page;// 当前页

    private static final Integer DEFAULT_SIZE = 20;// 每页大小，不从页面接收，而是固定大小
    private static final Integer DEFAULT_PAGE = 1;// 默认页

    private Map<String, String> selectedFilter;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if(page == null){
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public Map<String, String> getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(Map<String, String> selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}