package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.Brand;
import com.walmart.oms.mapper.BrandMapper;
import com.walmart.oms.service.BrandService;
import org.springframework.stereotype.Service;

/**
 * 品牌服务实现类
 */
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {
}