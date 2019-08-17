package com.leyou.search.listener;

import com.leyou.common.constants.MQConstants;
import com.leyou.search.service.GoodsService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ItemListener {
    @Autowired
    private GoodsService goodsService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstants.Queue.SEARCH_ITEM_UP,durable = "true"),
            exchange = @Exchange(name= MQConstants.Exchange.ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key =MQConstants.RoutingKey.ITEM_UP_KEY
        )
    )
    public void listenInsert(Long sid) {
        if(sid==null)
            return;
        goodsService.insertDoc(sid);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConstants.Queue.SEARCH_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(value = MQConstants.Exchange.ITEM_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = MQConstants.RoutingKey.ITEM_DOWN_KEY
    ))
    public void listenDel(Long sid) {
        if(sid==null)
            return;
        goodsService.deleteById(sid);
    }

}
