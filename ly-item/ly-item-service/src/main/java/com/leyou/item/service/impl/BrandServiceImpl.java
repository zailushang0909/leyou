package com.leyou.item.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.service.BrandService;
import com.leyou.item.entity.Brand;
import com.leyou.pojo.BrandDTO;
import com.leyou.item.entity.PageQuery;
import com.leyou.common.pojo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;


    @Override
    public PageResult<BrandDTO> queryBrandForPage(PageQuery pageQuery) {
        //1、使用分页插件设置分页查询
        Page page = PageHelper.startPage(pageQuery.getPage(), pageQuery.getRows());
        //2、判断查询条件是否为空 不为空则调用通用mapper 方法拼接条件语句
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(pageQuery.getKey())) {
            example.createCriteria().orLike("name", "%" + pageQuery.getKey() + "%").orEqualTo("letter", pageQuery.getKey());
        }
        //3、判断排序条件是否为空 不为空则调用通用mapper方法拼接排序条件
        if (StringUtils.isNotBlank(pageQuery.getSortBy())) {
            example.setOrderByClause(pageQuery.getSortBy()+" "+(pageQuery.getDesc() ? "DESC" : "ASC"));
        }
        //4、调用brandMapper方法查询分页数据
        List<Brand> select = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(select)) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        //5、组装分页结果对象ResultPage
        return new PageResult<BrandDTO>(page.getTotal(), BeanHelper.copyWithCollection(select, BrandDTO.class));
        //6、返回结果
    }

    @Override
    @Transactional
    public void insert(Brand brand, List<Long> cids) {
        //1、调用Brandmapper方法插入数据
        int count = brandMapper.insertSelective(brand);
        //2、如果结果为0 抛出异常
        if (count!=1) {
            throw new LyException(ExceptionEnum.FAIL_INSERT);
        }
        //3、调用Brandmapper方法向tb_category_brand表插入记录
        int k = brandMapper.insertCategoryAndBrand(brand.getId(),cids);
        if (k!=cids.size()) {
            throw new LyException(ExceptionEnum.FAIL_INSERT);
        }
        //4、如果结果为0则抛出异常
    }

    @Override
    @Transactional
    public void update(Brand brand, List<Long> cids) {
        //1、调用mapper更新
        brand.setUpdateTime(new Date());
        int count = brandMapper.updateByPrimaryKeySelective(brand);
        if (count!=1) {
            throw new LyException(ExceptionEnum.UPDATE_FAIL);
        }
        //2、根据brandId 查询分类id
        List<Long> cateGoryIds = brandMapper.queryCategoryIdsByBrandId(brand.getId());
        if (cateGoryIds.equals(cids)) {
            return;
        }
        //3、查询所有分类id下商品的数量
        List<Long> brandCountsOfCateGory = brandMapper.querySkuCountByCategoryIdsAndBrandId(cateGoryIds,brand.getId());
        //4、遍历取出有商品的分类id
        List<Long> canotDelCids = new ArrayList<>();
        for (int i = 0; i < cateGoryIds.size(); i++) {
            if (brandCountsOfCateGory.get(i) > 0) {
                canotDelCids.add(cateGoryIds.get(i));
            }
        }
        if (CollectionUtils.isEmpty(canotDelCids)) {
            //2、删除中间表数据
            brandMapper.deleteCategoryAndBrandByBid(brand.getId());
            count = brandMapper.insertCategoryAndBrand(brand.getId(), cids);
            //3、重建中间表数据
            if (count!=cids.size()) {
                throw new LyException(ExceptionEnum.UPDATE_FAIL);
            }
            return;
        }
        //5、判断新提交的分类id是否全包含有分类的id
        if (!cids.containsAll(canotDelCids)) {
            throw new LyException(ExceptionEnum.DELETE_FAIL);
        }
        //6、原来分类与有分类的id取差集
        cateGoryIds.removeAll(canotDelCids);
        if (!CollectionUtils.isEmpty(cateGoryIds)) {
            //7、差集批量删除
            count = brandMapper.deleteCategoryAndBrandByBidAndCids(cateGoryIds, brand.getId());
            if (count!=cateGoryIds.size()) {
                throw new LyException(ExceptionEnum.DELETE_FAIL);
            }
        }

        //7、新提交的分类id与有分类的id取差集
        cids.removeAll(canotDelCids);
        if (!CollectionUtils.isEmpty(cids)) {
            //8、差集插入
            count = brandMapper.insertCategoryAndBrand(brand.getId(),cids);
            if (count!=cids.size()) {
                throw new LyException(ExceptionEnum.DELETE_FAIL);
            }
        }

    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try {
            //1、按照id删除brand
            int count = brandMapper.deleteByPrimaryKey(id);
            if (count !=1) {
                throw new LyException(ExceptionEnum.DELETE_FAIL);
            }
           // 2、删除中间表数据
            brandMapper.deleteCategoryAndBrandByBid(id);
        } catch (LyException e) {
            throw new LyException(ExceptionEnum.DELETE_FAIL);
        }

    }

    @Override
    public BrandDTO queryBrandNameByBid(Long brandId) {
        Brand brand = brandMapper.selectByPrimaryKey(brandId);
        if (null==brand) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        return BeanHelper.copyProperties(brand,BrandDTO.class);
    }

    @Override
    public List<BrandDTO> queryBrandsByCid(Long cid) {
        List<Brand> brands = brandMapper.queryBrandsByCid(cid);
        if (CollectionUtils.isEmpty(brands)) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        return BeanHelper.copyWithCollection(brands, BrandDTO.class);
    }

}
