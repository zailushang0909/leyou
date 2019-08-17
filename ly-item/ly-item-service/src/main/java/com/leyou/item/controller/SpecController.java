package com.leyou.item.controller;

import com.leyou.item.service.SpecService;
import com.leyou.pojo.SpecGroupDTO;
import com.leyou.pojo.SpecParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<SpecParamDTO>> querySpecsByid(
            @RequestParam(value="gid",required = false) Long gid,
            @RequestParam(value="cid",required = false)Long cid,
            @RequestParam(value ="searching",required = false) Boolean searching) {
        return ResponseEntity.ok(specService.querySpecsByid(gid,cid,searching));
    }

    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroupDTO>> querySepcGroupsAndSpecsByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specService.querySepcGroupsAndSpecsByCid(cid));
    }
}
