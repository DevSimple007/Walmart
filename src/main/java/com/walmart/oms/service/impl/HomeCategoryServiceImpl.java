package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.HomeCategory;
import com.walmart.oms.mapper.HomeCategoryMapper;
import com.walmart.oms.service.HomeCategoryService;
import org.springframework.stereotype.Service;

@Service
public class HomeCategoryServiceImpl extends ServiceImpl<HomeCategoryMapper, HomeCategory> implements HomeCategoryService {
}
