package com.walmart.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walmart.oms.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
    List<ShoppingCart> selectCartWithProduct(@Param("memberId") Long memberId);
}
