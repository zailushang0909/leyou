package com.leyou.sms.listeners;

import com.leyou.common.constants.MQConstants;
import com.leyou.common.utils.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsListenerTest {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void testSms() {
        HashMap<String, String> msg = new HashMap<>();
        msg.put("phone", "13151243538");
        msg.put("code","123456");
        amqpTemplate.convertAndSend(MQConstants.Exchange.SMS_EXCHANGE_NAME, MQConstants.RoutingKey.VERIFY_CODE_KEY, msg);
    }

}