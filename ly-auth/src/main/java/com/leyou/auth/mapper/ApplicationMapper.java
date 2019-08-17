package com.leyou.auth.mapper;

import com.leyou.auth.entity.Application;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ApplicationMapper extends Mapper<Application> {
    List<Long> selectTargetIdsById(@Param("appId") Long id);
}
