package com.leyou.page.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.client.ItemClient;
import com.leyou.page.service.PageService;
import com.leyou.pojo.BrandDTO;
import com.leyou.pojo.CategoryDTO;
import com.leyou.pojo.SpecGroupDTO;
import com.leyou.pojo.SpuDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PageServiceImpl implements PageService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ItemClient itemClient;

    @Value("${ly.page.itemDir}")
    private String itemDir;
    @Value("${ly.page.itemTemplate}")
    private String itemTemplate;

    @Override
    public Map<String, Object> loadData(Long sid) {

        SpuDTO spu = itemClient.querySpuById(sid);

        List<SpecGroupDTO> specs = itemClient.querySepcGroupsAndSpecsByCid(spu.getCid3());

        // 查询分类集合
        List<CategoryDTO> categories = itemClient.getCategorysByIds(spu.getCids());
        // 查询品牌
        List<BrandDTO> collection = itemClient.queryBrandsByBids(Arrays.asList(spu.getBrandId()));
        BrandDTO brand = CollectionUtils.isEmpty(collection) ? null:collection.get(0);

        // 封装数据
        Map<String, Object> data = new HashMap<>();
        data.put("categories", categories);
        data.put("brand", brand);
        data.put("spuName", spu.getName());
        data.put("subTitle", spu.getSubTitle());
        data.put("skus", spu.getSkus());
        data.put("detail", spu.getSpuDetail());
        data.put("specs", specs);
        return data;
    }

    @Override
    public void createItemHtml(Long sid) {
        //根据id获取数据模型
        //将数据模型存入上下文
        Context context = new Context();
        context.setVariables(loadData(sid));
        //判断保存静态页面的文件夹存不存在 不存在则创建
        File dir = new File(itemDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new LyException(ExceptionEnum.DIRECTORY_WRITER_ERROR);
            }
        }
        //创建代表静态页的file对象
        //将静态页写入nginx路径
        try(PrintWriter printWriter = new PrintWriter(new File(dir, sid + ".html"), "utf-8")){
            templateEngine.process(itemTemplate,context,printWriter);
        } catch (IOException e) {
            log.error("【静态页服务】静态页生成失败，商品id：{}", sid);
            throw new LyException(ExceptionEnum.FILE_WRITER_ERROR);
        }
    }

    @Override
    public void deleteItemHtml(Long id) {
        File htmlFile = new File(itemDir,id + ".html");
        if (htmlFile.exists()) {
            if (!htmlFile.delete()) {
                log.error("【静态页服务】静态页删除失败，商品id：{}", id);
                throw new LyException(ExceptionEnum.DELETE_FAIL);
            }
        }
    }
}

