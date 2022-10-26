package com.fzh.crm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fzh.crm.controller.dto.UserDTO;
import com.fzh.crm.controller.dto.UserPasswordDTO;
import com.fzh.crm.entity.User;

import java.io.IOException;

public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    void updatePassword(UserPasswordDTO userPasswordDTO);

    Page<User> findPage(Page<User> objectPage, String username, String email, String address, String role);

    String sendEmail(String email) throws IOException;

    boolean addUser(String email);
}
