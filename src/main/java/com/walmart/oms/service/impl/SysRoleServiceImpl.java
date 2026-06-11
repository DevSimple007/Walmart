package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.SysRole;
import com.walmart.oms.entity.SysRoleMenu;
import com.walmart.oms.mapper.SysRoleMapper;
import com.walmart.oms.mapper.SysRoleMenuMapper;
import com.walmart.oms.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysRole> getByUserId(Long userId) {
        return sysRoleMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        // 删除原有的角色菜单关联
        sysRoleMenuMapper.deleteBatchIds(
                sysRoleMenuMapper.selectList(null)
                        .stream()
                        .filter(rm -> rm.getRoleId().equals(roleId))
                        .map(SysRoleMenu::getMenuId)
                        .collect(Collectors.toList())
        );

        // 保存新的角色菜单关联
        List<SysRoleMenu> roleMenus = menuIds.stream()
                .map(menuId -> {
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    return roleMenu;
                })
                .collect(Collectors.toList());

        if (!roleMenus.isEmpty()) {
            sysRoleMenuMapper.insertBatchSomeColumn(roleMenus);
        }
    }
}