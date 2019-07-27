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
    DATA_TRANSFER_ERROR(500, "数据转换出错")
    ;

    private int status;
    private String message;

    ExceptionEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
