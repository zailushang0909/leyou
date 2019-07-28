package com.leyou.item.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.service.BrandService;
import com.leyou.pojo.Brand;
import com.leyou.pojo.BrandDTO;
import com.leyou.pojo.PageQuery;
import com.leyou.pojo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

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
        int i = brandMapper.insertSelective(brand);
        //2、如果结果为0 抛出异常
        if (i==0) {
            throw new LyException(ExceptionEnum.FAIL_INSERT);
        }
        //3、调用Brandmapper方法向tb_category_brand表插入记录
        int k = brandMapper.insertCategoryAndBrand(brand.getId(),cids);
        if (k==0) {
            throw new LyException(ExceptionEnum.FAIL_INSERT);
        }
        //4、如果结果为0则抛出异常
    }
}
