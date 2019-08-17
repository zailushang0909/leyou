package com.leyou.search.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.client.ItemClient;
import com.leyou.pojo.*;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private ItemClient itemClient;
    @Autowired
    private ElasticsearchTemplate esTemplate;
    @Autowired
    private GoodsRepository goodsRepository;

    @Override
    public Goods buildGoods(Long spuId) {
        SpuDTO spuDTO = itemClient.querySpuById(spuId);
        if (spuDTO==null) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        //将SpuDTO中内容封装进Goods
        Goods goods = BeanHelper.copyProperties(spuDTO, Goods.class);
        goods.setCreateTime(spuDTO.getCreateTime().getTime());
        //将品牌id以及分类id封装进Goods（都在SpuDTO中）
        goods.setCategoryId(spuDTO.getCid3());
        goods.setBrandId(spuDTO.getBrandId());
        //查询spuDTO下所有Sku
        List<SkuDTO> skuDTOS = itemClient.querySkuBySpuId(spuDTO.getId());
        //遍历重新封装Sku装换成map 最终转换成Json 封装进Goods
        ArrayList<Map<String, Object>> skus = new ArrayList<>();
        HashSet<Long> prices = new HashSet<>();
        skuDTOS.forEach(skuDTO->{
            Map sku =  new HashMap<String,Object>();
            sku.put("id", skuDTO.getId());
            sku.put("title", skuDTO.getTitle());
            sku.put("image", StringUtils.substringBefore(skuDTO.getImages(),","));
            sku.put("price", skuDTO.getPrice());
            skus.add(sku);
            //遍历sku取出price封装进set集合
            prices.add(skuDTO.getPrice());
        });
        //将skus封装进goods
        goods.setSkus(JsonUtils.toString(skus));
        //将prices封装进goods
        goods.setPrice(prices);
        //获取分类下所有可搜索Spec
        List<SpecParamDTO> specParamDTOS = itemClient.querySpecsByid(null, spuDTO.getCid3(), true);
        //创建Map封装规格参数key为规格参数名字
        HashMap<String, Object> specs = new HashMap<>();
        //获取SpuDetail
        SpuDetailDTO spuDetailDTO = itemClient.querySpuDetailBySpuId(spuDTO.getId());
        //从SpuDetail通用规格参数map
        Map<String, Object> genericSpec = JsonUtils.nativeRead(spuDetailDTO.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        //从SpuDetail特有规格参数map
        Map<String, Object> SpecialSpec = JsonUtils.nativeRead(spuDetailDTO.getSpecialSpec(), new TypeReference<Map<String, Object>>() {
        });
        specParamDTOS.forEach(specParamDTO -> {
            Object value = null;
            if (specParamDTO.getGeneric()) {
                value = genericSpec.get(specParamDTO.getId().toString());
            } else {
                value = SpecialSpec.get(specParamDTO.getId().toString());
            }
            //值如果数字类型就装化成范围拼接上单位存进map
            if (specParamDTO.getNumeric()) {
                //TODO 装换成范围
                value = chooseSegment(value, specParamDTO);
            }
            //值如果不是数字则直接存储到mao
            specs.put(specParamDTO.getName(), value);
        });
        goods.setSpecs(specs);
        //根据从SpuDTO中获取分类名、将其中的\换成空格 然后拼接上品牌名称 之后封装进Goods的all属性
        String categoryNames = itemClient.getCategorysByIds(spuDTO.getCids())
                .stream()
                .map(CategoryDTO::getName)
                .collect(Collectors.joining(" "));
        BrandDTO brandDTO = itemClient.queryBrandsByBids(Arrays.asList(spuDTO.getBrandId())).get(0);
        String brandName = brandDTO.getName();
        goods.setAll(categoryNames.concat(" ").concat(brandName));
        return goods;
    }

    @Override
    public PageResult<GoodsDTO> page(SearchRequest searchRequest) {
        BoolQueryBuilder boolQueryBuilder = baseQueryBuild(searchRequest);
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withQuery(boolQueryBuilder);
        searchQueryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1,searchRequest.getSize()));
        AggregatedPage<Goods> goods = esTemplate.queryForPage(searchQueryBuilder.build(), Goods.class);
        List<Goods> content = goods.getContent();
        if (CollectionUtils.isEmpty(content)) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        return new PageResult<>(goods.getTotalElements(),goods.getTotalPages(), BeanHelper.copyWithCollection(content,GoodsDTO.class));
    }

    @Override
    public Map<String, List<?>> queryFilter(SearchRequest searchRequest) {
        BoolQueryBuilder boolQueryBuilder = baseQueryBuild(searchRequest);
        LinkedHashMap<String, List<?>> filter = new LinkedHashMap<>();
        //聚合获取品牌和分类
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        searchQueryBuilder.withQuery(boolQueryBuilder);
        searchQueryBuilder.withPageable(PageRequest.of(0, 1));
        searchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        searchQueryBuilder.addAggregation(AggregationBuilders.terms("品牌").field("brandId"));
        searchQueryBuilder.addAggregation(AggregationBuilders.terms("分类").field("categoryId"));
        AggregatedPage<Goods> goods = esTemplate.queryForPage(searchQueryBuilder.build(), Goods.class);
        LongTerms brandIdsTerms = goods.getAggregations().get("品牌");
        List<LongTerms.Bucket> brandIdsbuckets = brandIdsTerms.getBuckets();
        List<Long> brandIds = brandIdsbuckets.stream()
                .map(LongTerms.Bucket::getKeyAsNumber)
                .map(Number::longValue)
                .collect(Collectors.toList());
        //跨服务根据brandidsDTO 查询brandid
        List<BrandDTO> brandDTOS = itemClient.queryBrandsByBids(brandIds);
        filter.put("品牌", brandDTOS);
        LongTerms categoryIdsTerms = goods.getAggregations().get("分类");
        List<LongTerms.Bucket> categoryIdsbuckets = categoryIdsTerms.getBuckets();
        List<Long> categoryIds = categoryIdsbuckets.stream()
                .map(LongTerms.Bucket::getKeyAsNumber)
                .map(Number::longValue)
                .collect(Collectors.toList());
        //跨服务查询categoryDTO
        List<CategoryDTO> categoryDTOS = itemClient.getCategorysByIds(categoryIds);
        filter.put("分类", categoryDTOS);
        //如果分类不为1则响应给前端
        if (categoryDTOS!=null &&categoryDTOS.size()==1) {
            //如果分类为1则查询可搜索规格参数
            buildSpecs(searchRequest,categoryDTOS.get(0).getId(),filter);
        }
        return filter;
    }

    @Override
    public void insertDoc(Long sid) {
        goodsRepository.save(buildGoods(sid));
    }

    @Override
    public void deleteById(Long sid) {
        goodsRepository.deleteById(sid);
    }

    private BoolQueryBuilder baseQueryBuild(SearchRequest searchRequest) {
        if (searchRequest.getKey()==null) {
            throw new LyException(ExceptionEnum.PARAM_ERROR);
        }
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND));
        Map<String, String> selectedFilter = searchRequest.getSelectedFilter();
        if (!CollectionUtils.isEmpty(selectedFilter)) {
            selectedFilter.keySet().forEach(filter->{
                if (filter.equals("品牌")) {
                    filter = "brandId";
                } else if (filter.equals("分类")) {
                    filter = "categoryId";
                } else {
                    filter = "specs." + filter + ".keyword";
                }
                boolQueryBuilder.filter(QueryBuilders.termQuery(filter, selectedFilter.get(filter)));
            });
        }
        return boolQueryBuilder;
    }

    private void buildSpecs(SearchRequest searchRequest,Long cid,LinkedHashMap<String, List<?>> filter) {
        List<SpecParamDTO> specParamDTOS = itemClient.querySpecsByid(null, cid, true);
        //1、遍历specParamDTOS组装聚合条件
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = baseQueryBuild(searchRequest);
        searchQueryBuilder.withQuery(boolQueryBuilder);
        searchQueryBuilder.withPageable(PageRequest.of(0, 1));
        searchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        specParamDTOS.forEach(specParamDTO-> searchQueryBuilder.addAggregation(AggregationBuilders.terms(specParamDTO.getName()).field("specs." + specParamDTO.getName() + ".keyword")));
        //2、查询聚合
        AggregatedPage<Goods> goods = esTemplate.queryForPage(searchQueryBuilder.build(), Goods.class);
        Aggregations aggregations = goods.getAggregations();
        //3、解析聚合结果封装进filter
        specParamDTOS.forEach(specParamDTO -> {
            StringTerms stringTerms =aggregations.get(specParamDTO.getName());
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
            List<String> collect = buckets.stream()
                    .map(StringTerms.Bucket::getKeyAsString)
                    .collect(Collectors.toList());
            filter.put(specParamDTO.getName(), collect);
        });

    }

    private String chooseSegment(Object value, SpecParamDTO p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }
}
