package com.fzh.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fzh.crm.entity.Menu;

import java.util.List;

public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
