package com.fzh.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzh.crm.entity.Role;

import java.util.List;

public interface IRoleService extends IService<Role> {

    void setRoleMenu(Integer roleId, List<Integer> menuIds);

    List<Integer> getRoleMenu(Integer roleId);
}
