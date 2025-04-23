package com.example.springboot.service.impl;

import com.example.springboot.entity.RoleMenu;
import com.example.springboot.mapper.RoleMenuMapper;
import com.example.springboot.service.IRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色菜单关系表 服务实现类
 * </p>
 *
 * @author 代刘斌
 * @since 2023-09-29
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {

}
