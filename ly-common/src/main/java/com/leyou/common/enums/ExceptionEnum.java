package com.leyou.common.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {

    /**
     * 价格不能为空
     */
    PRICE_CANNOT_BYNULL(400, "价格不能为空"),
    /**
     * 数据转换出错
     */
    DATA_TRANSFER_ERROR(500, "数据转换出错"),
    CATEGORY_NOT_EXIST(204, "分类不存在"),
    OPTIONS_NOT_EXIST(204,"资源不存在"),
    FAIL_INSERT(500, "新增失败")
    ;

    private int status;
    private String message;

    ExceptionEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
