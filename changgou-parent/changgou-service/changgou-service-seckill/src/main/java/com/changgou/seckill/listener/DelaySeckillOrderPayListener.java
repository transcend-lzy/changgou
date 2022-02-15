package com.changgou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import entity.SystemConstants;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 监听秒杀的队列
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.seckill.listener *
 * @since 1.0
 */
@Component
@RabbitListener(queues = "orderListenerSeckillQueue")
public class DelaySeckillOrderPayListener {


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @RabbitHandler
    public void consumer(String message) throws Exception {

        //获取用户排队信息
        SeckillStatus seckillStatus = JSON.parseObject(message, SeckillStatus.class);
        //如果此时redis中没有用户排队信息，则表明该订单已经处理了，就没事，如果没有就说明用户一直没付款，就要处理
        Object o = redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).get(seckillStatus.getUsername());

        if(o != null) {
            //关闭微信支付

            //删除订单

        }


    }
}
