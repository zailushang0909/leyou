package com.leyou.sms.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.leyou.sms.constants.SmsConstants.*;


@Slf4j
@Component
public class SmsHelper {

    private IAcsClient client;//账户访问客户端

    private SmsProperties prop;//属性信息

    public SmsHelper(IAcsClient client, SmsProperties prop) {
        this.client = client;
        this.prop = prop;
    }

    //短信发出的方法
    public void sendMessage(String phone, String param) {
        CommonRequest request = new CommonRequest();
        request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain(prop.getDomain());
        request.setVersion(prop.getVersion());
        request.setAction(prop.getAction());
        request.putQueryParameter(SMS_PARAM_KEY_PHONE, phone);
        request.putQueryParameter(SMS_PARAM_KEY_SIGN_NAME, prop.getSignName());
        request.putQueryParameter(SMS_PARAM_KEY_TEMPLATE_CODE, prop.getVerifyCodeTemplate());
        request.putQueryParameter(SMS_PARAM_KEY_TEMPLATE_PARAM, param);

        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println("response = " + response);
            if(response.getHttpStatus() >= 300){
                log.error("【SMS服务】发送短信失败。响应信息：{}", response.getData());
            }
            // 获取响应体
            Map<String, String> resp = JsonUtils.toMap(response.getData(), String.class, String.class);
            // 判断是否是成功
            if(!StringUtils.equals(OK, resp.get(SMS_RESPONSE_KEY_CODE))){
                // 不成功，
                log.error("【SMS服务】发送短信失败，原因{}", resp.get(SMS_RESPONSE_KEY_MESSAGE));
                throw new LyException(ExceptionEnum.SEND_MESSAGE_ERROR);
            }
            log.info("【SMS服务】发送短信成功，手机号：{}, 响应：{}", phone, response.getData());
        } catch (ServerException e) {
            log.error("【SMS服务】发送短信失败，服务端异常。", e);
        } catch (ClientException e) {
            log.error("【SMS服务】发送短信失败，客户端异常。", e);
        }
    }
}