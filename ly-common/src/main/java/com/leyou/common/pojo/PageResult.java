package com.leyou.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    /**
     * this.brands = resp.data.items;
     * this.totalBrands = resp.data.total;
     */
    private Long total;
    private List<T> items;
}
