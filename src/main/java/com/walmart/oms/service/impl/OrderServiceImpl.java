package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.dto.OrderCreateRequest;
import com.walmart.oms.entity.Member;
import com.walmart.oms.entity.MemberLevel;
import com.walmart.oms.entity.Order;
import com.walmart.oms.entity.OrderItem;
import com.walmart.oms.entity.Product;
import com.walmart.oms.exception.BusinessException;
import com.walmart.oms.mapper.MemberLevelMapper;
import com.walmart.oms.mapper.MemberMapper;
import com.walmart.oms.mapper.OrderItemMapper;
import com.walmart.oms.mapper.OrderMapper;
import com.walmart.oms.mapper.ProductMapper;
import com.walmart.oms.entity.Coupon;
import com.walmart.oms.entity.MemberCoupon;
import com.walmart.oms.mapper.CouponMapper;
import com.walmart.oms.mapper.MemberCouponMapper;
import com.walmart.oms.service.MemberCouponService;
import com.walmart.oms.service.MemberService;
import com.walmart.oms.service.OrderService;
import com.walmart.oms.utils.PageUtils;
import com.walmart.oms.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private MemberLevelMapper memberLevelMapper;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberCouponMapper memberCouponMapper;

    @Autowired
    private MemberCouponService memberCouponService;

    @Autowired
    private CouponMapper couponMapper;

    private final Random random = new Random();

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<Order> page = this.page(
                new Query<Order>().getPage(params),
                new QueryWrapper<Order>()
        );
        return new PageUtils(page);
    }

    @Override
    public void updateStatus(Long orderId, Integer status) {
        Order order = this.getById(orderId);
        if (order != null) {
            order.setStatus(status);
            order.setUpdateTime(LocalDateTime.now());
            this.updateById(order);
        }
    }

    @Override
    public void handleRefund(Long orderId) {
        Order order = this.getById(orderId);
        if (order != null) {
            order.setStatus(4);
            order.setUpdateTime(LocalDateTime.now());
            this.updateById(order);
        }
    }

    @Override
    @Transactional
    public Order createOrder(Long memberId, OrderCreateRequest request) {
        List<OrderCreateRequest.OrderItemRequest> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new BusinessException("订单商品不能为空");
        }

        // 获取会员信息及等级折扣
        Member member = memberMapper.selectById(memberId);
        BigDecimal discount = BigDecimal.ONE; // 默认无折扣
        if (member != null && member.getLevelId() != null) {
            MemberLevel level = memberLevelMapper.selectById(member.getLevelId());
            if (level != null && level.getDiscount() != null) {
                discount = level.getDiscount();
            }
        }

        // 生成订单号
        String orderNo = "ORD" + System.currentTimeMillis() + String.format("%04d", random.nextInt(10000));

        // 验证库存并计算总金额（应用会员折扣）
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderCreateRequest.OrderItemRequest itemReq : items) {
            Product product = productMapper.selectById(itemReq.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException("商品[" + itemReq.getProductId() + "]不存在或已下架");
            }
            if (product.getStock() < itemReq.getQuantity()) {
                throw new BusinessException("商品[" + product.getProductName() + "]库存不足，当前库存：" + product.getStock());
            }
            // 单价 × 数量 × 折扣
            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()))
                    .multiply(discount)
                    .setScale(2, RoundingMode.HALF_UP);
            totalAmount = totalAmount.add(itemTotal);
        }

        // 处理优惠券
        BigDecimal couponDiscount = BigDecimal.ZERO;
        Long couponId = null;
        if (request.getCouponMemberId() != null) {
            MemberCoupon mc = memberCouponMapper.selectById(request.getCouponMemberId());
            if (mc != null && mc.getStatus() == 0 && mc.getMemberId().equals(memberId)) {
                Coupon coupon = couponMapper.selectById(mc.getCouponId());
                if (coupon != null && coupon.getStatus() == 1) {
                    // 验证优惠券有效期
                    LocalDateTime now = LocalDateTime.now();
                    boolean timeValid = true;
                    if (coupon.getStartTime() != null && now.isBefore(coupon.getStartTime())) {
                        timeValid = false;
                    }
                    if (coupon.getEndTime() != null && now.isAfter(coupon.getEndTime())) {
                        timeValid = false;
                    }

                    // 验证最低消费门槛
                    boolean amountValid = true;
                    if (coupon.getMinAmount() != null && totalAmount.compareTo(coupon.getMinAmount()) < 0) {
                        amountValid = false;
                    }

                    if (timeValid && amountValid) {
                        couponId = coupon.getId();
                        if (coupon.getCouponType() == 2) {
                            // 折扣券: couponValue 是折扣比例(如0.15表示8.5折, 即减15%)
                            couponDiscount = totalAmount.multiply(coupon.getCouponValue()).setScale(2, RoundingMode.HALF_UP);
                        } else {
                            // 满减/立减券
                            couponDiscount = coupon.getCouponValue();
                        }
                        // 优惠金额不能超过订单金额
                        if (couponDiscount.compareTo(totalAmount) > 0) {
                            couponDiscount = totalAmount;
                        }
                    }
                }
            }
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(memberId);
        order.setTotalAmount(totalAmount);
        order.setCouponDiscount(couponDiscount);
        order.setActualAmount(totalAmount.subtract(couponDiscount));
        order.setCouponId(couponId);
        order.setRemark(request.getRemark());
        order.setDeliveryType(request.getDeliveryType());
        order.setAddress(request.getAddress());
        order.setPhone(request.getPhone());
        order.setDeliveryTime(request.getDeliveryTime());
        order.setStatus(0); // 待支付
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        this.save(order);

        // 创建订单明细 & 扣减库存
        for (OrderCreateRequest.OrderItemRequest itemReq : items) {
            Product product = productMapper.selectById(itemReq.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(itemReq.getProductId());
            orderItem.setQuantity(itemReq.getQuantity());
            // 单价也应用折扣
            BigDecimal discountedPrice = product.getPrice().multiply(discount).setScale(2, RoundingMode.HALF_UP);
            orderItem.setPrice(discountedPrice);
            orderItem.setCreateTime(LocalDateTime.now());
            orderItemMapper.insert(orderItem);

            // 扣减库存
            product.setStock(product.getStock() - itemReq.getQuantity());
            productMapper.updateById(product);
        }

        // 标记优惠券为已使用
        if (request.getCouponMemberId() != null && couponId != null) {
            memberCouponService.useCoupon(memberId, couponId, orderNo);
        }

        // 加载订单明细返回
        order.setItems(getOrderItems(order.getId()));
        return order;
    }

    @Override
    public IPage<Order> getMemberOrders(Long memberId, Integer status, Integer pageNum, Integer pageSize) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", memberId);
        if (status != null && status >= 0) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Order getOrderDetail(String orderNo, Long memberId) {
        Order order = this.getOne(new QueryWrapper<Order>()
                .eq("order_no", orderNo).eq("user_id", memberId));
        if (order != null) {
            order.setItems(getOrderItems(order.getId()));
        }
        return order;
    }

    private List<OrderItem> getOrderItems(Long orderId) {
        List<OrderItem> items = orderItemMapper.selectList(
                new QueryWrapper<OrderItem>().eq("order_id", orderId));
        // 填充商品名称
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                item.setProductName(product.getProductName());
            }
        }
        return items;
    }
}
