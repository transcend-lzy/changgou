package com.changgou.order.queue;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chaoyue
 * @data2022-02-11 19:08
 */
@Configuration
public class QueueConfig {
    /***
     * 创建queue1，延时队列会过期，过期后将数据转发给queue2
     */
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable("orderDelayQueue")
                .withArgument("x-dead-letter-exchange", "orderListenerExchange")   //orderDelayQueue队列信息会过期，过期之后进入死信队列，这里绑定死信队列交换机
                //过了一定时间后该信息还没有被读取，就是死信了
                .withArgument("x-dead-letter-routing-key","orderListenerQueue")    //死信路由到orderListenerQueue
                .build();

    }

    /***
     * 创建queue2
     */
    @Bean
    public Queue orderListenerQueue(){
        return new Queue("orderListenerQueue", true);
    }





    /***
     * 创建交换机
     */

    @Bean
    public Exchange orderListenerExchange() {
        return new DirectExchange("orderListenerExchange");
    }

    /***
     * 队列queue2绑定Exchange
     */

    @Bean
    public Binding orderListenerBinding(Queue orderListenerQueue, Exchange orderListenerExchange) {
        return BindingBuilder.bind(orderListenerQueue).to(orderListenerExchange).with("orderListenerQueue").noargs();
    }
}
