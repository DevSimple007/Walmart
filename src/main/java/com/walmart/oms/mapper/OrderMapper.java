package com.walmart.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walmart.oms.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    BigDecimal sumAmountByDate(@Param("date") String date);

    Integer countByDate(@Param("date") String date);

    List<Map<String, Object>> countByStatus();

    List<Map<String, Object>> salesByDay(@Param("startDate") String startDate);

    List<Map<String, Object>> salesByWeek(@Param("startDate") String startDate);

    List<Map<String, Object>> salesByMonth(@Param("startDate") String startDate);

    List<Map<String, Object>> topProducts(@Param("limit") int limit);
}