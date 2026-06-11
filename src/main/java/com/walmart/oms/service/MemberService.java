package com.walmart.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.Member;
import com.walmart.oms.utils.PageUtils;

import java.util.Map;

public interface MemberService extends IService<Member> {
    PageUtils queryPage(Map<String, Object> params);
    void updatePoints(Long memberId, Integer points, Integer type);
    void updateBalance(Long memberId, Double amount, Integer type);
    void updateLevel(Long memberId);
    Member login(String phone, String password);
    Member register(String phone, String password, String memberName);
    Member getByPhone(String phone);
}