package com.changgou.seckill.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chaoyue
 * @data2022-02-12 15:04
 */
@Configuration
public class QueueConfig {
    /***
     * 创建queue1，延时队列会过期，过期后将数据转发给queue2
     */
    @Bean
    public Queue orderDelaySeckillQueue() {
        return QueueBuilder.durable("orderDelaySeckillQueue")
                .withArgument("x-dead-letter-exchange", "orderListenerSeckillExchange")   //orderDelayQueue队列信息会过期，过期之后进入死信队列，这里绑定死信队列交换机
                //过了一定时间后该信息还没有被读取，就是死信了
                .withArgument("x-dead-letter-routing-key","orderListenerSeckillQueue")    //死信路由到orderListenerQueue
                .build();

    }

    /***
     * 创建queue2
     */
    @Bean
    public Queue orderListenerSeckillQueue(){
        return new Queue("orderListenerSeckillQueue", true);
    }





    /***
     * 创建交换机
     */

    @Bean
    public Exchange orderListenerSeckillExchange() {
        return new DirectExchange("orderListenerSeckillExchange");
    }

    /***
     * 队列queue2绑定Exchange
     */

    @Bean
    public Binding orderListenerBinding() {
        //队列queue2绑定Exchange，将消息都路由到queue2
        return BindingBuilder.bind(orderListenerSeckillQueue()).to(orderListenerSeckillExchange()).with("orderListenerSeckillQueue").noargs();
    }
}
