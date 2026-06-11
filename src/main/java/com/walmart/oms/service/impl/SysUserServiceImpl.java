package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.SysUser;
import com.walmart.oms.exception.BusinessException;
import com.walmart.oms.mapper.SysUserMapper;
import com.walmart.oms.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    @Override
    public SysUser login(String username, String password) {
        // 根据用户名查询用户
        SysUser user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已禁用");
        }

        // 验证密码（暂时使用明文验证，方便测试）
        if (!password.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        return user;
    }
}