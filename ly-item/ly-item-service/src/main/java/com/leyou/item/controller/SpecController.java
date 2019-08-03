package com.leyou.item.controller;

import com.leyou.item.service.SpecService;
import com.leyou.pojo.SpecGroupDTO;
import com.leyou.pojo.SpecParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> querySepcGroupsByCid(@RequestParam("id") Long cid) {
        return ResponseEntity.ok(specService.querySepcGroupsByCid(cid));
    }

    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> querySpecsByGid(
            @RequestParam(value="gid",required = false) Long gid,
            @RequestParam(value="cid",required = false)Long cid) {
        return ResponseEntity.ok(specService.querySpecsByGid(gid,cid));
    }


}
