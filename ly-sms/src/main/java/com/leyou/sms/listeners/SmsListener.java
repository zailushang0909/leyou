package com.leyou.sms.listeners;

import com.leyou.common.constants.MQConstants;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.utils.SmsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SmsListener {

    @Autowired
    private SmsHelper smsHelper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.Queue.SMS_VERIFY_CODE_QUEUE),
            exchange = @Exchange(name = MQConstants.Exchange.SMS_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key= MQConstants.RoutingKey.VERIFY_CODE_KEY
    ))
    public void smsListener(Map<String,String> msg) {
        if (msg==null) {
            return;
        }
        String phone = msg.remove("phone");
        if (StringUtils.isBlank(phone)) {
            return;
        }
        try {
            smsHelper.sendMessage(phone, JsonUtils.toString(msg));
        } catch (LyException e) {
            log.error("【SMS服务】短信验证码发送失败",e);
        }

    }

}
