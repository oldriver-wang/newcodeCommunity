package com.hao.community.controller;


import com.google.code.kaptcha.Producer;
import com.hao.community.entity.User;
import com.hao.community.service.UserService;
import com.hao.community.util.CommunityConstant;

import com.hao.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    // 定义一个方法处理注册请求  post请求
    // 声明一个User对象接受数据
    // 若传入的东西与 user对象相匹配， 则Spring自动注入属性


    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            // 跳转到激活页面
            // 注册成功调到首页去  不是登录页面 还需要待激活
            model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送激活邮件,请尽快激活!");
            model.addAttribute("target", "/index");

            return "/site/operate-result";
            // 优化  跳转到一个第三方页面  成功了 马上跳转 (5s)
        }
        else {
            // 失败的情况
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }


    @RequestMapping(path = "/activation/{userId}/{code}", method=RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        // 全部都先跳转到中转页面
        // 成功就从中转页面跳到
        if(result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        }else if(result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经激活过了!");
            model.addAttribute("target", "/index");
        }else {
            model.addAttribute("msg", "激活失败，提供的激活码不正确");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }


    // 浏览器访问它， 它就返回一个图片，浏览器可以看到
    // 将他改成动态路径 login.html
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // cookie和session  放在session比较合适
        //生成文字和 图片
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        System.out.println(text);
        // 将文本保存到session
        session.setAttribute("kaptcha", text);

        // 将图片直接输出给浏览器

        //先申明返回的是什么格式的数据
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败: " + e.getMessage());
        }
    }

    // session 需要得到验证码   cookie 浏览器保存ticket
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model, HttpSession session, HttpServletResponse response) {

        // 先检查验证码
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "/site/login";
        }

        // 检查账号, 密码
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        // 若存在ticket 则登陆成功  将ticket存入cookie  并将cookie放入response
        if(map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            // 如果是类对象  实体  则Spring会直接装在model里
            // 但是若是普通对象  不会放在model里
            //1、自己添加到model
            // 2、 从request 当中取值
            //   在thymeleaf  param.username 相当于  request.getParameter("username")
            model.addAttribute("username", username);
            model.addAttribute("username", password);
            model.addAttribute("code", code);
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("codeMsg", map.get("codeMsg"));
            return "/site/login";
        }

    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue(name = "ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }
}
