package com.leyou.item.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.pojo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "item-service")
public interface ItemClient {

    @GetMapping("/spu/page")
    PageResult<SpuDTO> querySpusPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "10") Integer rows);

    @GetMapping("sku/of/spu")
    List<SkuDTO> querySkuBySpuId(@RequestParam("id") Long spuId);

    @GetMapping("spec/params")
    List<SpecParamDTO> querySpecsByid(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching);

    @GetMapping("spu/detail")
    SpuDetailDTO querySpuDetailBySpuId(@RequestParam("id") Long spuId);

    @GetMapping("brand/of/ids")
    List<BrandDTO> queryBrandsByBids(@RequestParam("bids") List<Long> bids);

    @GetMapping("category/of/ids")
    List<CategoryDTO> getCategorysByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping("spu/of/{spuId}")
    SpuDTO querySpuById(@PathVariable("spuId") Long spuId);

    @GetMapping("spec/{cid}")
    List<SpecGroupDTO> querySepcGroupsAndSpecsByCid(@PathVariable("cid") Long cid);

    @GetMapping("/sku/list")
    List<SkuDTO> querySkusByIds(@RequestParam("ids") List<Long> ids);


}
