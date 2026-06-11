package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.ShippingAddress;
import com.walmart.oms.mapper.ShippingAddressMapper;
import com.walmart.oms.service.ShippingAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ShippingAddressServiceImpl extends ServiceImpl<ShippingAddressMapper, ShippingAddress> implements ShippingAddressService {

    @Override
    public List<ShippingAddress> getByMemberId(Long memberId) {
        return list(new QueryWrapper<ShippingAddress>()
                .eq("member_id", memberId)
                .orderByDesc("is_default")
                .orderByDesc("create_time"));
    }

    @Override
    @Transactional
    public void setDefault(Long memberId, Long addressId) {
        // 先取消所有默认
        ShippingAddress reset = new ShippingAddress();
        reset.setIsDefault(0);
        update(reset, new QueryWrapper<ShippingAddress>().eq("member_id", memberId));
        // 设置新的默认
        ShippingAddress addr = getById(addressId);
        if (addr != null && addr.getMemberId().equals(memberId)) {
            addr.setIsDefault(1);
            updateById(addr);
        }
    }
}
