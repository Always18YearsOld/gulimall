package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author ltb
 * @email lvtianbao@qq.com
 * @date 2020-07-01 03:55:45
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
