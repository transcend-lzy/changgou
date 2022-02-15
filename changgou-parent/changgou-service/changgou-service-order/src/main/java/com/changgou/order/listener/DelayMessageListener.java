package com.changgou.order.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 过期信息监听
 * @author chaoyue
 * @data2022-02-11 19:34
 */
@Component
@RabbitListener(queues = "orderListenerQueue")
public class DelayMessageListener {
    /***
     *延时队列监听
     * @param message
     */
    @RabbitHandler
    public void getDelayMessage(String message) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("监听消息时间" + simpleDateFormat.format(new Date()));
        System.out.println("监听到的消息" + message);

        //监听到之后取消订单回滚就可以了
    }
}
