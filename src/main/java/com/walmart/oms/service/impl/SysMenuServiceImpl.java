package com.walmart.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.walmart.oms.entity.SysMenu;
import com.walmart.oms.mapper.SysMenuMapper;
import com.walmart.oms.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> getByRoleId(Long roleId) {
        return sysMenuMapper.selectByRoleId(roleId);
    }

    @Override
    public List<SysMenu> getByUserId(Long userId) {
        return sysMenuMapper.selectByUserId(userId);
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        // 构建菜单树
        List<SysMenu> menuTree = new ArrayList<>();
        // 先找出所有的根菜单（parentId为0）
        List<SysMenu> rootMenus = menus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .collect(Collectors.toList());

        // 为每个根菜单递归构建子菜单
        for (SysMenu rootMenu : rootMenus) {
            buildChildren(rootMenu, menus);
            menuTree.add(rootMenu);
        }

        return menuTree;
    }

    /**
     * 递归构建子菜单
     * @param parentMenu 父菜单
     * @param menus 所有菜单列表
     */
    private void buildChildren(SysMenu parentMenu, List<SysMenu> menus) {
        List<SysMenu> children = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentMenu.getId()))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            parentMenu.setChildren(children);
            for (SysMenu child : children) {
                buildChildren(child, menus);
            }
        }
    }
}