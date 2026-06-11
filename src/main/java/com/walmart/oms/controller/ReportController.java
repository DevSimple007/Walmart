package com.walmart.oms.controller;

import com.walmart.oms.mapper.OrderMapper;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/report")
@Tag(name = "销售报表")
public class ReportController {

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/sales")
    @Operation(summary = "销售统计")
    public R sales(@RequestParam String type) {
        String startDate;
        switch (type) {
            case "week" -> startDate = LocalDate.now().minusWeeks(4).format(DateTimeFormatter.ISO_DATE);
            case "month" -> startDate = LocalDate.now().minusMonths(12).format(DateTimeFormatter.ISO_DATE);
            default -> startDate = LocalDate.now().minusDays(30).format(DateTimeFormatter.ISO_DATE);
        }

        List<Map<String, Object>> data;
        switch (type) {
            case "week" -> data = orderMapper.salesByWeek(startDate);
            case "month" -> data = orderMapper.salesByMonth(startDate);
            default -> data = orderMapper.salesByDay(startDate);
        }

        // 如果数据库没有数据，返回空数组而不是null
        if (data == null) data = new ArrayList<>();

        return R.ok().put("data", data);
    }

    @GetMapping("/top")
    @Operation(summary = "销量排行")
    public R top() {
        List<Map<String, Object>> data = orderMapper.topProducts(10);
        if (data == null) data = new ArrayList<>();

        // 添加排名
        for (int i = 0; i < data.size(); i++) {
            data.get(i).put("rank", i + 1);
        }

        return R.ok().put("data", data);
    }

    @GetMapping("/profit")
    @Operation(summary = "利润分析")
    public R profit() {
        // 使用最近12个月的销售数据作为利润分析
        String startDate = LocalDate.now().minusMonths(12).format(DateTimeFormatter.ISO_DATE);
        List<Map<String, Object>> salesData = orderMapper.salesByMonth(startDate);
        if (salesData == null) salesData = new ArrayList<>();

        List<Map<String, Object>> data = new ArrayList<>();
        for (Map<String, Object> item : salesData) {
            Map<String, Object> profitItem = new HashMap<>();
            profitItem.put("month", item.get("month"));
            Number sales = (Number) item.get("sales");
            double salesVal = sales.doubleValue();
            profitItem.put("sales", salesVal);
            // 假设成本为销售额的60%
            double cost = Math.round(salesVal * 0.6 * 100.0) / 100.0;
            profitItem.put("cost", cost);
            profitItem.put("profit", Math.round((salesVal - cost) * 100.0) / 100.0);
            data.add(profitItem);
        }

        return R.ok().put("data", data);
    }
}
