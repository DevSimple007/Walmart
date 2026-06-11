package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.Member;
import com.walmart.oms.entity.MemberLevel;
import com.walmart.oms.entity.MemberPoints;
import com.walmart.oms.mapper.MemberMapper;
import com.walmart.oms.mapper.MemberLevelMapper;
import com.walmart.oms.mapper.MemberPointsMapper;
import com.walmart.oms.exception.BusinessException;
import com.walmart.oms.service.MemberService;
import com.walmart.oms.utils.PageUtils;
import com.walmart.oms.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private MemberLevelMapper memberLevelMapper;

    @Autowired
    private MemberPointsMapper memberPointsMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<Member> page = this.page(
                new Query<Member>().getPage(params),
                new QueryWrapper<Member>()
        );
        return new PageUtils(page);
    }

    @Override
    public void updatePoints(Long memberId, Integer points, Integer type) {
        Member member = this.getById(memberId);
        if (member != null) {
            member.setPoints(member.getPoints() + points);
            this.updateById(member);

            // 记录积分变动
            MemberPoints memberPoints = new MemberPoints();
            memberPoints.setMemberId(memberId);
            memberPoints.setMemberName(member.getMemberName());
            memberPoints.setPoints(points);
            memberPoints.setType(type);
            memberPoints.setRemark(type == 1 ? "积分增加" : "积分减少");
            memberPointsMapper.insert(memberPoints);

            // 更新会员等级
            updateLevel(memberId);
        }
    }

    @Override
    public void updateBalance(Long memberId, Double amount, Integer type) {
        Member member = this.getById(memberId);
        if (member != null) {
            BigDecimal currentBalance = member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO;
            BigDecimal changeAmount = BigDecimal.valueOf(amount);
            if (type == 1) {
                member.setBalance(currentBalance.add(changeAmount));
            } else {
                member.setBalance(currentBalance.subtract(changeAmount));
            }
            this.updateById(member);
        }
    }

    @Override
    public void updateLevel(Long memberId) {
        Member member = this.getById(memberId);
        if (member != null) {
            MemberLevel level = memberLevelMapper.selectOne(
                    new QueryWrapper<MemberLevel>()
                            .le("min_points", member.getPoints())
                            .orderByDesc("min_points")
                            .last("LIMIT 1")
            );
            if (level != null) {
                member.setLevelId(level.getId());
                this.updateById(member);
            }
        }
    }

    @Override
    public Member login(String phone, String password) {
        Member member = getByPhone(phone);
        if (member == null) {
            throw new BusinessException("手机号或密码错误");
        }
        if (!password.equals(member.getPassword())) {
            throw new BusinessException("手机号或密码错误");
        }
        return member;
    }

    @Override
    public Member register(String phone, String password, String memberName) {
        Member existing = getByPhone(phone);
        if (existing != null) {
            throw new BusinessException("该手机号已注册");
        }
        Member member = new Member();
        member.setPhone(phone);
        member.setPassword(password);
        member.setMemberName(memberName);
        member.setLevelId(1L);
        member.setPoints(0);
        member.setBalance(BigDecimal.ZERO);
        this.save(member);
        return member;
    }

    @Override
    public Member getByPhone(String phone) {
        return this.getOne(new QueryWrapper<Member>().eq("phone", phone));
    }
}