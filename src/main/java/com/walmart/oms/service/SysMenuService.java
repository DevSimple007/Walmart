package com.walmart.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.SysMenu;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface SysMenuService extends IService<SysMenu> {
    /**
     * 根据角色ID查询菜单列表
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<SysMenu> getByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单列表
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> getByUserId(Long userId);

    /**
     * 构建菜单树
     * @param menus 菜单列表
     * @return 菜单树
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);
}