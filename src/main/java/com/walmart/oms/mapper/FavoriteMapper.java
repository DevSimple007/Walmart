package com.walmart.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walmart.oms.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
    List<Favorite> selectFavoritesWithProduct(@Param("memberId") Long memberId);
}
