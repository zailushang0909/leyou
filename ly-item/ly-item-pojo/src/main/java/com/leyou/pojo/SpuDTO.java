package com.leyou.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class SpuDTO {
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String name;// 商品名称
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    private String categoryName;//
    private String brandName;
    private List<SkuDTO> skus;
    private SpuDetailDTO spuDetail;
    private Date createTime;

    @JsonIgnore
    public List<Long> getCids() {
        return Arrays.asList(cid1,cid2,cid3);
    }

}
