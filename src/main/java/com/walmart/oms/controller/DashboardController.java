package com.walmart.oms.controller;

import com.walmart.oms.mapper.OrderMapper;
import com.walmart.oms.mapper.ProductMapper;
import com.walmart.oms.mapper.MemberMapper;
import com.walmart.oms.mapper.ProductCategoryMapper;
import com.walmart.oms.utils.R;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.walmart.oms.entity.Product;
import com.walmart.oms.entity.ProductCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "仪表盘")
public class DashboardController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @GetMapping("/stats")
    @Operation(summary = "获取仪表盘统计数据")
    public R stats() {
        Map<String, Object> data = new HashMap<>();

        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE);

        // 今日销售额
        BigDecimal todaySales = orderMapper.sumAmountByDate(today);
        BigDecimal yesterdaySales = orderMapper.sumAmountByDate(yesterday);
        data.put("salesToday", todaySales);
        data.put("salesYesterday", yesterdaySales);
        data.put("salesChange", calculateChange(todaySales, yesterdaySales));

        // 今日订单数
        int todayOrders = orderMapper.countByDate(today);
        int yesterdayOrders = orderMapper.countByDate(yesterday);
        data.put("ordersToday", todayOrders);
        data.put("ordersYesterday", yesterdayOrders);
        data.put("ordersChange", calculateChange(todayOrders, yesterdayOrders));

        // 库存预警（库存小于10的商品）
        long lowStockCount = productMapper.selectCount(
            new QueryWrapper<Product>().lt("stock", 10).eq("status", 1)
        );
        data.put("inventoryAlert", lowStockCount);

        // 会员总数
        long memberCount = memberMapper.selectCount(null);
        data.put("memberCount", memberCount);

        // 订单状态分布
        List<Map<String, Object>> statusData = orderMapper.countByStatus();
        data.put("orderStatus", statusData);

        // 商品分类分布
        List<ProductCategory> categories = productCategoryMapper.selectList(null);
        List<Map<String, Object>> categoryData = new ArrayList<>();
        for (ProductCategory cat : categories) {
            long count = productMapper.selectCount(
                new QueryWrapper<Product>().eq("category_id", cat.getId()).eq("status", 1)
            );
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("value", count);
                item.put("name", cat.getCategoryName());
                categoryData.add(item);
            }
        }
        data.put("categoryDistribution", categoryData);

        return R.ok().put("data", data);
    }

    private double calculateChange(Number today, Number yesterday) {
        double t = today.doubleValue();
        double y = yesterday.doubleValue();
        if (y == 0) return t > 0 ? 100.0 : 0.0;
        return Math.round((t - y) / y * 1000.0) / 10.0;
    }
}
