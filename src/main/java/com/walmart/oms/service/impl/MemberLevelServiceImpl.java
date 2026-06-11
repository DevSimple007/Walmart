package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.MemberLevel;
import com.walmart.oms.mapper.MemberLevelMapper;
import com.walmart.oms.service.MemberLevelService;
import com.walmart.oms.utils.PageUtils;
import com.walmart.oms.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelMapper, MemberLevel> implements MemberLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLevel> page = this.page(
                new Query<MemberLevel>().getPage(params),
                new QueryWrapper<MemberLevel>()
        );
        return new PageUtils(page);
    }
}