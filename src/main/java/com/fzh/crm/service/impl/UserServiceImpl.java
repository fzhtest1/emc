package com.fzh.crm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzh.crm.common.Constants;
import com.fzh.crm.common.RoleEnum;
import com.fzh.crm.config.EmailIdentify;
import com.fzh.crm.controller.dto.UserDTO;
import com.fzh.crm.controller.dto.UserPasswordDTO;
import com.fzh.crm.entity.Menu;
import com.fzh.crm.entity.User;
import com.fzh.crm.exception.ServiceException;
import com.fzh.crm.mapper.RoleMapper;
import com.fzh.crm.mapper.RoleMenuMapper;
import com.fzh.crm.mapper.UserMapper;
import com.fzh.crm.service.IMenuService;
import com.fzh.crm.service.IUserService;
import com.fzh.crm.utils.TokenUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Log LOG = Log.get();

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Resource
    private IUserService userService;

    @Value("${spring.mail.username}")
    private String MAIL_SENDER; //邮件发送者

    @Resource
    private JavaMailSender javaMailSender;//注入QQ发送邮件的bean

    @Override
    public UserDTO login(UserDTO userDTO) {
        User one = getUserInfo(userDTO);
        if (one != null) {
            BeanUtil.copyProperties(one, userDTO, true);
            // 设置token
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDTO.setToken(token);

            String role = one.getRole(); // ROLE_ADMIN
            // 设置用户的菜单列表
            List<Menu> roleMenus = getRoleMenus(role);
            userDTO.setMenus(roleMenus);

            //管理员监控登录
            SimpleMailMessage checkMail = new SimpleMailMessage();
            checkMail.setFrom(MAIL_SENDER);
            checkMail.setTo(MAIL_SENDER);
            checkMail.setSubject("教务系统有新用户登录");
            checkMail.setText("请注意，用户：" + userDTO.getNickname() + "的用户正尝试登录系统");
            javaMailSender.send(checkMail);

            return userDTO;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }
    }

    @Override
    public User register(UserDTO userDTO) {
        User one = getUserInfo(userDTO);
        if (one == null) {
            one = new User();
            //将userDTO复制一份到one中
            BeanUtil.copyProperties(userDTO, one, true);
            // 默认一个普通用户的角色
            one.setRole(RoleEnum.ROLE_TEACHER.toString());
            save(one);  // 把 copy完之后的用户对象存储到数据库
        } else {
            throw new ServiceException(Constants.CODE_600, "用户已存在");
        }
        return one;
    }

    @Override
    public void updatePassword(UserPasswordDTO userPasswordDTO) {
        int update = userMapper.updatePassword(userPasswordDTO);
        if (update < 1) {
            throw new ServiceException(Constants.CODE_600, "密码错误");
        }
    }

    @Override
    public Page<User> findPage(Page<User> page, String username, String email, String address, String role) {
        return userMapper.findPage(page, username, email, address, role);
    }

    @Override
    public String sendEmail(String email) throws IOException {
        //String code = RandomUtil.randomNumbers(4); // 随机一个 4位长度的验证码
        EmailIdentify identify = new EmailIdentify();
        String code = identify.captcha(email);
        try {
            //管理员监控登录
            SimpleMailMessage checkMail = new SimpleMailMessage();
            checkMail.setFrom(MAIL_SENDER);
            checkMail.setTo(MAIL_SENDER);
            checkMail.setSubject("教务系统有新用户登录");
            checkMail.setText("请注意，邮箱为：" + email + "的用户正尝试登录系统");
            javaMailSender.send(checkMail);

            SimpleMailMessage mailMessage= new SimpleMailMessage();
            mailMessage.setFrom(MAIL_SENDER);//发送者
            mailMessage.setTo(email);//接收者
            mailMessage.setSubject("欢迎登录本管理系统");//邮件标题
            mailMessage.setText("请接收好您的验证码：" + code + "，此验证码将在5分钟后过期");//邮件内容
            javaMailSender.send(mailMessage);//发送邮箱

            return "发送成功";
        } catch (Exception e) {
            return "邮件发送失败";
        }
    }

    @Override
    public boolean addUser(String email) {
        Boolean go = true;
        String code = "";
        while (go) {
            code = RandomUtil.randomNumbers(4); // 随机一个4位长度的数字名称
            //判断用户名称是否存在
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("username",code);
            List<User> one = userService.list(userQueryWrapper);
            if (one!=null) {
                go = false;
            }
        }
        User user = new User();
        user.setEmail(email);
        user.setUsername(code);
        user.setNickname(code);
        user.setPassword("123");
        user.setRole("ROLE_TEACHER");
        boolean res = userService.save(user);
        return res;
    }

    private User getUserInfo(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try {
            one = getOne(queryWrapper); // 从数据库查询用户信息
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }

    /**
     * 获取当前角色的菜单列表
     * @param roleFlag
     * @return
     */
    private List<Menu> getRoleMenus(String roleFlag) {
        Integer roleId = roleMapper.selectByFlag(roleFlag);
        // 当前角色的所有菜单id集合
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        // 查出系统所有的菜单(树形)
        List<Menu> menus = menuService.findMenus("");
        // new一个最后筛选完成之后的list
        List<Menu> roleMenus = new ArrayList<>();
        // 筛选当前用户角色的菜单
        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())) {
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            // removeIf()  移除 children 里面不在 menuIds集合中的 元素
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
    }

}
