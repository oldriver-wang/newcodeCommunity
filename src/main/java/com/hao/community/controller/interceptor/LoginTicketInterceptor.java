package com.hao.community.controller.interceptor;

import com.hao.community.entity.LoginTicket;
import com.hao.community.entity.User;
import com.hao.community.service.UserService;
import com.hao.community.util.CookieUtil;
import com.hao.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    // 用来安全保存访问user  线程安全

    // 先找到 ticket  从request 得到cookie封装到一个函数里面
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String ticket = CookieUtil.getValue(request, "ticket");
        // 若有ticket  有凭证
        if (ticket != null) {
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效
            if(ticket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User userById = userService.findUserById(loginTicket.getUserId());
                // 在本次请求持有用户
                // 考虑并发问题  不能简单存放在一个容器下
                // 每个线程单独存放一份
                hostHolder.setUsers(userById);
            }
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
