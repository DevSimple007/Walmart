package com.walmart.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.MemberLevel;
import com.walmart.oms.utils.PageUtils;

import java.util.Map;

public interface MemberLevelService extends IService<MemberLevel> {
    PageUtils queryPage(Map<String, Object> params);
}