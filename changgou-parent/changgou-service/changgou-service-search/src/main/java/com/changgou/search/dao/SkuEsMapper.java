package com.changgou.search.dao;

import com.changgou.goods.pojo.Sku;
import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.search.dao *
 * @since 1.0
 */
//SkuInfo是sku对应的映射bean， Long是skuinfo的主键类型
public interface SkuEsMapper extends ElasticsearchRepository<SkuInfo,Long> {
}
