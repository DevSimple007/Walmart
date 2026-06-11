package com.walmart.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.ShippingAddress;
import java.util.List;

public interface ShippingAddressService extends IService<ShippingAddress> {
    List<ShippingAddress> getByMemberId(Long memberId);
    void setDefault(Long memberId, Long addressId);
}
