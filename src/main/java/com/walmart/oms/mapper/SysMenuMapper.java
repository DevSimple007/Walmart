package com.walmart.oms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.walmart.oms.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单Mapper接口
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * 根据角色ID查询菜单列表
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<SysMenu> selectByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单列表
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectByUserId(Long userId);
}