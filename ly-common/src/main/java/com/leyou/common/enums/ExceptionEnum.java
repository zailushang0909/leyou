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
    FAIL_INSERT(500, "新增失败"),
    UPLOAD_IMAGE_FAIL(500, "图片上传失败"),
    UPDATE_FAIL(500,"更新失败"),
    DELETE_FAIL(500,"删除失败"),
    CANNOT_UPLOAD_NULL(400,"图片不能为空"),
    TYPE_NOT_ALLOW(400,"图片类型不允许"),
    NOT_ONE_PICTURE(400,"不是图片"),
    FILE_READ_FAIL(500,"图片读取失败"),
    COMMON_FAIL(500,"服务器内部错误"),
    PARAM_ERROR(400,"参数错误"),
    DIRECTORY_WRITER_ERROR(500,"路径创建失败"),
    FILE_WRITER_ERROR(500,"静态页生成失败"),
    SEND_MESSAGE_ERROR(500,"发送短信失败"),
    VERIFY_CODE_FAIL(400,"验证码已过期或者验证码错误"),
    LOGIN_FAIL(400,"用户名或者密码有误"),
    LOAD_PRIVATEKEY_FAIL(500,"加载密钥失败"),
    INVALID_TOKEN(401,"token无效")
    ;

    private int status;
    private String message;

    ExceptionEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
