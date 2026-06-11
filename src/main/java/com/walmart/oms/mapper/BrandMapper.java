package com.walmart.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walmart.oms.entity.Brand;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌Mapper接口
 */
@Mapper
public interface BrandMapper extends BaseMapper<Brand> {
}