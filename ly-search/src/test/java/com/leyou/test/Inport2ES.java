/*
package com.leyou.test;

import com.leyou.LySearchApplication;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.pojo.SpuDTO;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class Inport2ES {

    @Autowired
    private ElasticsearchTemplate ESTemplate;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private GoodsService goodsService;

    @Test
    public void creatIndex() {
        boolean index = ESTemplate.createIndex(Goods.class);
        boolean b = ESTemplate.putMapping(Goods.class);
        System.out.println("index = " + index);
        System.out.println("b = " + b);
    }

    @Test
    public void importGoods2ES() {
        Integer page = 1;
        while (true) {
            PageResult<SpuDTO> spuDTOPageResult = itemClient.querySpusPage(null, true, page, 20);
            if (spuDTOPageResult==null) {
                return;
            }
            List<SpuDTO> items = spuDTOPageResult.getItems();
            List<Goods> goods = items.stream()
                    .map(spuDTO -> goodsService.buildGoods(spuDTO))
                    .collect(Collectors.toList());
            goodsRepository.saveAll(goods);
            page++;
        }

    }

}
*/
