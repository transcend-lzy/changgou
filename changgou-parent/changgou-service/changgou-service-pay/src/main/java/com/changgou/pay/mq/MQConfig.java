package com.changgou.pay.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;



/**
 * @author chaoyue
 * @data2022-02-11 16:41
 */
@Configuration
public class MQConfig {
    /***
     * 读取配置文件中得信息得对象
     */
    @Autowired
    private Environment env;
    /***
     * 创建DirectExchange交换机
     * @return
     */
    @Bean
    public DirectExchange basicExchange(){
        //交换机名称、是否持久化、是否自动删除
        return new DirectExchange(env.getProperty("mq.pay.exchange.order"), true,false);
    }

    /***
     * 创建队列
     * @return
     */
    @Bean(name = "queueOrder")
    public Queue queueOrder(){

        return new Queue(env.getProperty("mq.pay.queue.order"), true);
    }

    /****
     * 队列绑定到交换机上
     * @return
     */
    @Bean
    public Binding basicBinding(){
        return BindingBuilder.bind(queueOrder()).to(basicExchange()).with(env.getProperty("mq.pay.routing.key"));
    }

    /********************************秒杀队列创建*****************************/

    /***
     * 创建DirectExchange交换机
     * @return
     */
    @Bean
    public DirectExchange basicSeckillExchange(){
        //交换机名称、是否持久化、是否自动删除
        return new DirectExchange(env.getProperty("mq.pay.exchange.seckillorder"), true,false);
    }

    /***
     * 创建队列
     * @return
     */
    @Bean
    public Queue queueSeckillOrder(){

        return new Queue(env.getProperty("mq.pay.queue.seckillorder"), true);
    }

    /****
     * 队列绑定到交换机上
     * @return
     */
    @Bean
    public Binding basicSeckillBinding(){
        return BindingBuilder.bind(queueSeckillOrder()).to(basicSeckillExchange()).with(env.getProperty("mq.pay.routing.seckillkey"));
    }
}
