package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.constants.MQConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.Spu;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.service.BrandService;
import com.leyou.item.service.CategoryService;
import com.leyou.item.service.GoodsService;
import com.leyou.pojo.SkuDTO;
import com.leyou.pojo.SpuDTO;
import com.leyou.pojo.SpuDetailDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

import static com.leyou.common.constants.MQConstants.Exchange.ITEM_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.RoutingKey.ITEM_DOWN_KEY;
import static com.leyou.common.constants.MQConstants.RoutingKey.ITEM_UP_KEY;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public PageResult<SpuDTO> querySpusPage(String key, Boolean saleable, Integer page, Integer rows) {
        //1、设置分页
        PageHelper.startPage(page, rows);
        //2、创建条件模板

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //3、判断key是否为空
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%");
        }
        //4、判断saleable是否为空
        if (null!=saleable) {
            criteria.andEqualTo("saleable", saleable);
        }
        //5、按照模板查询
        List<Spu> spus = spuMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(spus)) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        //6、将获取总记录数
        PageInfo pageInfo = new PageInfo(spus);
        //7、将结果装换成SpuDTO
        //8、将总记录数、SpuDTO集合封装进分页结果集返回
        List<SpuDTO> spuDTOS = BeanHelper.copyWithCollection(spus, SpuDTO.class);
        spuDTOS.forEach(spuDTO -> {
            List<Long> cids = spuDTO.getCids();
            String catgoryName = categoryService.getCategorysByCids(cids).stream()
                    .map(category -> category.getName())
                    .collect(Collectors.joining("/"));
            spuDTO.setCategoryName(catgoryName);
            spuDTO.setBrandName(brandService.queryBrandNameByBid(spuDTO.getBrandId()).getName());
        });
        //查询商品分类和品牌封装进spuDTO
        return new PageResult<SpuDTO>(pageInfo.getTotal(), spuDTOS);
    }

    @Override
    @Transactional
    public void insertGoods(SpuDTO spuDTO) {
        //1、插入spu
        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
        int count = spuMapper.insertSelective(spu);
        if (count!=1) {
            throw new LyException(ExceptionEnum.FAIL_INSERT);
        }
        //2、插入spuDetail
        SpuDetail spuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), SpuDetail.class);
        Long spuId = spu.getId();
        spuDetail.setSpuId(spuId);
        count = spuDetailMapper.insertSelective(spuDetail);
        if (count!=1) {
            throw new LyException(ExceptionEnum.FAIL_INSERT);
        }
        //3、插入sku
        List<SkuDTO> skuDTOS = spuDTO.getSkus();
        List<Sku> skus = BeanHelper.copyWithCollection(skuDTOS, Sku.class);
        skus.forEach(sku -> sku.setSpuId(spuId));
        count = skuMapper.insertList(skus);
        if (count!=skus.size()) {
            throw new LyException(ExceptionEnum.FAIL_INSERT);
        }
        //TODO 发送  insert routingkey 以及 spuid 给消息中间件
        amqpTemplate.convertAndSend(ITEM_EXCHANGE_NAME,ITEM_UP_KEY,spuDTO.getId());
    }

    @Override
    public SpuDetailDTO querySpuDetailBySpuId(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if (spuDetail==null) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        return BeanHelper.copyProperties(spuDetail, SpuDetailDTO.class);
    }

    @Override
    public List<SkuDTO> querySkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        return BeanHelper.copyWithCollection(skus, SkuDTO.class);
    }

    @Override
    @Transactional
    public void updateGoods(SpuDTO spuDTO) {
        //1、更新spu
        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count!=1) {
            throw new LyException(ExceptionEnum.UPDATE_FAIL);
        }
        //2、更新spudetail
        SpuDetail spuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), SpuDetail.class);
        count = spuDetailMapper.updateByPrimaryKeySelective(spuDetail);
        if (count!=1) {
            throw new LyException(ExceptionEnum.UPDATE_FAIL);
        }

        //3、更新sku
        //3.1转换成Sku对象集合
        List<Sku> skus = BeanHelper.copyWithCollection(spuDTO.getSkus(), Sku.class);
        //3.2获取新增Sku
        List<Sku> newSkus = skus.stream().filter(sku -> sku.getId() == null).collect(Collectors.toList());
        //3.3获取网页提交上来的原来已经有的Sku
        skus.removeAll(newSkus);
        //3.4获取表中原来所有的Sku
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> oldSkus = skuMapper.select(sku);
        //3.5获取原来表格中有的现在提交上来没有的sku（删除了的sku）
        for (Sku s : skus) {
            oldSkus.removeIf(oldSku -> oldSku.getId() .equals(s.getId()) );
        }
        if (!CollectionUtils.isEmpty(oldSkus)) {
            count = skuMapper.deleteByIdList(oldSkus.stream().map(Sku::getId).collect(Collectors.toList()));
            if (count!=oldSkus.size()) {
                throw new LyException(ExceptionEnum.UPDATE_FAIL);
            }
        }
        count = 0;
        for (Sku sk : skus) {
            count += skuMapper.updateByPrimaryKeySelective(sk);
        }
        if (count!= skus.size()) {
            throw new LyException(ExceptionEnum.UPDATE_FAIL);
        }
        //3.6如果新增Sku不为空则执行批量插入
        if (!CollectionUtils.isEmpty(newSkus)) {
            newSkus.forEach(newSku-> newSku.setSpuId(spu.getId()));
            count = skuMapper.insertList(newSkus);
            if (count!=newSkus.size()) {
                throw new LyException(ExceptionEnum.UPDATE_FAIL);
            }
        }

    }

    @Override
    @Transactional
    public void  updateSaleable(Long spuId, Boolean saleable) {
        //1、更新spu为下架状态
        Spu spu = new Spu();
        spu.setId(spuId);
        spu.setSaleable(saleable);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        //2、健壮性判断
        if (count!=1) {
            throw new LyException(ExceptionEnum.UPDATE_FAIL);
        }
        //3、查询根据spuid 查询sku数量
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        int total = skuMapper.selectCount(sku);
        //4、更新sku enable状态
        count = skuMapper.updateEnableBySpuId(spuId, saleable);
        //5、健壮性判断
        if (total!=count) {
            throw new LyException(ExceptionEnum.UPDATE_FAIL);
        }

        //TODO 发送  insert routingkey 以及 spuid 给消息中间件
        amqpTemplate.convertAndSend(ITEM_EXCHANGE_NAME,saleable ? ITEM_UP_KEY:ITEM_DOWN_KEY,spuId);
    }

    @Override
    public SpuDTO querySpuById(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if (spu==null) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spu.getId());

        SpuDTO spuDTO = BeanHelper.copyProperties(spu, SpuDTO.class);
        spuDTO.setSpuDetail(BeanHelper.copyProperties(spuDetail,SpuDetailDTO.class));

        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skus = skuMapper.select(sku);
        spuDTO.setSkus(BeanHelper.copyWithCollection(skus, SkuDTO.class));

        return spuDTO;
    }

    @Override
    public List<SkuDTO> querySkusByIds(List<Long> ids) {
        List<Sku> skus = this.skuMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(skus)) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        return BeanHelper.copyWithCollection(skus, SkuDTO.class);
    }
}
