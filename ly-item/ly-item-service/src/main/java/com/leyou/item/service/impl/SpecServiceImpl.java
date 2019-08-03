package com.leyou.item.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.service.SpecService;
import com.leyou.item.entity.SpecGroup;
import com.leyou.pojo.SpecGroupDTO;
import com.leyou.item.entity.SpecParam;
import com.leyou.pojo.SpecParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecServiceImpl implements SpecService {


    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;

    @Override
    public List<SpecGroupDTO> querySepcGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);
        if (CollectionUtils.isEmpty(specGroups)) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        return BeanHelper.copyWithCollection(specGroups,SpecGroupDTO.class);
    }

    @Override
    public List<SpecParamDTO> querySpecsByGid(Long gid, Long cid) {

        if ((gid==null && cid==null)||(gid!=null && cid!=null)) {
            throw new LyException(ExceptionEnum.PARAM_ERROR);
        }
        SpecParam specParam = new SpecParam();
        specParam.setCid(cid);
        specParam.setGroupId(gid);
        List<SpecParam> specParams = this.specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(specParams)) {
            throw new LyException(ExceptionEnum.OPTIONS_NOT_EXIST);
        }
        return BeanHelper.copyWithCollection(specParams,SpecParamDTO.class);

    }
}
