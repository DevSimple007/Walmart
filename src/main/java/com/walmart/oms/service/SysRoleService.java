package com.walmart.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.walmart.oms.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IService<SysRole> {
    /**
     * 根据用户ID查询角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> getByUserId(Long userId);

    /**
     * 保存角色菜单关联
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     */
    void saveRoleMenus(Long roleId, List<Long> menuIds);
}