package com.changgou.goods.dao;
import com.changgou.goods.pojo.Sku;
import com.changgou.order.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/****
 * @Author:admin
 * @Description:Sku的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface SkuMapper extends Mapper<Sku> {
    //这里用mysql实现，主要是为了利用innodb的行级锁特性，此时只能允许一个事务修改该记录，只有等该事务结束后，其他事务才能操作
    @Update(value="update tb_sku set num=num-#{num},sale_num=sale_num+#{num} where id =#{id} and num >=#{num}")
    public int decrCount(@Param(value = "id") Long id,@Param(value = "num") Integer num);
}
