package com.changgou.seckill.task;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import entity.IdWorker;
import entity.SystemConstants;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.seckill.task *
 * @since 1.0
 */
@Component
public class MultiThreadingCreateOrder {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    //创建订单 异步处理 加入一个注解
    @Async   //加上该注解后，该方法异步执行，底层还是用线程池
    public void createrOrder(){


        //从队列中获取抢单信息 左存右取
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(SystemConstants.SEC_KILL_USER_QUEUE_KEY).rightPop();

        if(seckillStatus!=null) {
            String time = seckillStatus.getTime();
            Long id = seckillStatus.getGoodsId();//秒杀商品的ID
            String username = seckillStatus.getUsername();

            //1.根据商品的ID 获取秒杀商品的数据
            //多线程抢单时可能会出现超卖现象，因为不同线程进来时查询当前库存数量的时候，
            // 有可能其他线程查完了创建了订单，但是还没抢库存，这时候就会判定可以下单，产生超卖现象
            //判断 先从队列中获取商品 ,如果能获取到,说明 有库存,如果获取不到,说明 没库存 卖完了 return.
            //解决超卖
            Object o = redisTemplate.boundListOps(SystemConstants.SEC_KILL_CHAOMAI_LIST_KEY_PREFIX + id).rightPop();
            if(o==null){   //这里这样清理可能会有问题（和SeckillOrderServiceImpl同时看），可能这里删除的时候并发排队，刚删除完同一个用户就排队了
                //发现没有重复排队的记录，就生成了，但这回商品已经买完了，进入不了这里所以无法删除这个记录
                //该用户购买其他商品也会有重复排队的问题，但实际上他没有重复排队，所以设置重复排队k和排队表示的队列应该设置超时时间
                //卖完了
                //清除 掉  防止重复排队的key
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).delete(username);
                //清除 掉  排队标识(存储用户的抢单信息)
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).delete(username);
                return ;
            }

            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).get(id);
            //前面解决超卖了，后面就不用判断库存了
//            if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
//                throw new RuntimeException("卖完了");
//            }

            //3.如果有库存

            //4.创建一个预订单
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());//订单的ID
            seckillOrder.setSeckillId(id);//秒杀商品ID
            seckillOrder.setMoney(seckillGoods.getCostPrice());//金额
            seckillOrder.setUserId(username);//登录的用户名
            seckillOrder.setCreateTime(new Date());//创建时间
            seckillOrder.setStatus("0");//未支付
            redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).put(username, seckillOrder);

            //5.减库存
            seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);

            //6.判断库存是是否为0  如果 是,更新到数据库中,删除掉redis中的秒杀商品
//            if (seckillGoods.getStockCount() <= 0) {   这种判断方式会有问题，如果还剩两个商品，并发两个请求，同时库存递减，
//            这种情况下实际上应该进入if，但此时因为并发问题导致库存不是0 是1，不进入判断导致出问题
            Long size = redisTemplate.boundListOps(SystemConstants.SEC_KILL_CHAOMAI_LIST_KEY_PREFIX + id).size();
            if (size <= 0) {
                seckillGoods.setStockCount(size.intValue());
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);//数据库的库存更新为0
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).delete(id);
            } else {
                //设置回redis中
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).put(id, seckillGoods);

            }
            //7.创建订单成功()


            try {
                System.out.println("开始模拟下单操作=====start====" + new Date() + Thread.currentThread().getName());
                Thread.sleep(10000);
                System.out.println("开始模拟下单操作=====end====" + new Date() + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //创建订单成功了 修改用户的抢单的信息
            seckillStatus.setOrderId(seckillOrder.getId());
            seckillStatus.setStatus(2);//
            seckillStatus.setMoney(Float.valueOf(seckillOrder.getMoney()));
            //重新设置回redis中，更新抢单状态
            redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).put(username,seckillStatus);

            //发送消息给延迟队列，保证用户长时间不付款就取消订单回滚库存
            rabbitTemplate.convertAndSend("orderDelaySeckillQueue", (Object) JSON.toJSONString(seckillStatus), new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setExpiration("10000");
                    return message;
                }
            });

        }

    }
}
