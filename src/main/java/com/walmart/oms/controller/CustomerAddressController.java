package com.walmart.oms.controller;

import com.walmart.oms.entity.ShippingAddress;
import com.walmart.oms.service.ShippingAddressService;
import com.walmart.oms.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/customer/address")
@Tag(name = "顾客收货地址", description = "顾客端收货地址接口")
public class CustomerAddressController {

    @Autowired
    private ShippingAddressService addressService;

    @GetMapping("/list")
    @Operation(summary = "收货地址列表")
    public R<List<ShippingAddress>> list(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("userId");
        return R.success(addressService.getByMemberId(memberId));
    }

    @PostMapping("/add")
    @Operation(summary = "添加收货地址")
    public R<?> add(HttpServletRequest request, @RequestBody ShippingAddress address) {
        Long memberId = (Long) request.getAttribute("userId");
        address.setMemberId(memberId);
        addressService.save(address);
        return R.ok();
    }

    @PutMapping("/update")
    @Operation(summary = "更新收货地址")
    public R<?> update(@RequestBody ShippingAddress address) {
        addressService.updateById(address);
        return R.ok();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除收货地址")
    public R<?> delete(@PathVariable Long id) {
        addressService.removeById(id);
        return R.ok();
    }

    @PutMapping("/setDefault/{id}")
    @Operation(summary = "设置默认地址")
    public R<?> setDefault(HttpServletRequest request, @PathVariable Long id) {
        Long memberId = (Long) request.getAttribute("userId");
        addressService.setDefault(memberId, id);
        return R.ok();
    }
}
