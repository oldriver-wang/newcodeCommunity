package com.hao.community.service;

import com.hao.community.dao.LoginTicketMapper;
import com.hao.community.dao.UserMapper;
import com.hao.community.entity.LoginTicket;
import com.hao.community.entity.User;
import com.hao.community.util.CommunityConstant;
import com.hao.community.util.CommunityUtil;
import com.hao.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;


    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    // 配置文件中的域名和项目名
    @Value("${community.path.domain}")
    private String domain;


    // 项目名称
    @Value("${server.servlet.context-path}")
    private String contextPath;


    public Map<String, Object> register(User user) {
        // 返回的多个status 封装在map里
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if(StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 验证账号
        User u = userMapper.selectByUsername(user.getUsername());
        if(u!=null){
            map.put("usernameMsg", "该账号已存在!");
        }

        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg", "该邮箱已被已注册!");
        }

        // 注册用户
        // 密码加密 : salt加密
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        // 普通用户
        user.setType(0);

        // 没有激活  需要激活
        user.setStatus(0);

        // 激活码
        user.setActivationCode(CommunityUtil.generateUUID());

        // 随机头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));

        user.setCreateTime(new Date());
        // 插入用户
        int i = userMapper.insertUser(user);
        System.out.println(i);
        Context context = new Context();
        context.setVariable("email", user.getEmail());

        // http://localhost:8080/community/activation/101/code
        // 这里的Id 是 mybatis.configuration.useGeneratedKeys=true  mybatis自动生成并设置的
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        //设置激活链接
        context.setVariable("url", url);
        // 设置邮件内容  这里有渲染模板
        String content = templateEngine.process("/mail/activation", context);

        // 发送激活邮件
        mailClient.sendMail(user.getEmail(), "牛客网激活账号", content);
        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus()==1) // 已经激活
            return ACTIVATION_REPEAT;
        else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }


    public User findUserById(int id) {
        return userMapper.selectById(id);
        // 为外键显示用户名称
    }

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    // 登录业务逻辑
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String,Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        User user = userMapper.selectByUsername(username);
        // 验证账号
        if(user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }
        // 验证状态
        if(user.getStatus() == 0) {
            map.put("usernameMsg","该账号未激活!");
            return map;
        }

        // 验证密码
        if(!CommunityUtil.md5(password+user.getSalt()).equals(user.getPassword())) {
            map.put("usernameMsg","密码错误!");
            return map;
        }


        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds*1000));

        loginTicketMapper.insertLoginTicket(loginTicket);
        // 若返回成功了  需要把凭证发过去  可以发送对象 也可以只接受ticket   浏览器只需要ticket
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    // @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public void logout(String ticket) {
        // LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        loginTicketMapper.updateStatus(ticket, 1);

    }

    // 加上查询凭证的代码
    public LoginTicket findLoginTicket(String ticket)
    {

        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword) {
        Map<String, Object> map = new HashMap<>();
        // 判断和原密码一致不
        User user = userMapper.selectById(userId);
        if (!CommunityUtil.md5(oldPassword + user.getSalt()).equals(user.getPassword())) {
            map.put("passwordMsg", "原密码错误");
            return map;
        }
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(userId, newPassword);
        return map;
    }
}
