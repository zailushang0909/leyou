package com.leyou.pojo;

import lombok.Data;

@Data
public class PageQuery {
    private String key;
    private Integer page=1;
    private Integer rows=10;
    private String sortBy;
    private Boolean desc;
}
