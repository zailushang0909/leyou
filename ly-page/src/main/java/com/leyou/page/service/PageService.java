package com.leyou.page.service;

import java.util.Map;

public interface PageService {

    Map<String, Object> loadData(Long sid);

    void createItemHtml(Long sid);

    void deleteItemHtml(Long id);
}
