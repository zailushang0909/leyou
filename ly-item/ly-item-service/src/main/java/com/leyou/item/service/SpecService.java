package com.leyou.item.service;

import com.leyou.pojo.SpecGroupDTO;
import com.leyou.pojo.SpecParamDTO;

import java.util.List;

public interface SpecService {
    List<SpecGroupDTO> querySepcGroupsByCid(Long cid);

    List<SpecParamDTO> querySpecsByid(Long gid, Long cid, Boolean searching);

    List<SpecGroupDTO> querySepcGroupsAndSpecsByCid(Long cid);
}
