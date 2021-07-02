package com.hao.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


// 从request 访问cookie  封装为一个函数方便调用
public class CookieUtil {
    public static String getValue(HttpServletRequest request, String name) {
        if(request == null || name == null) {
            throw new IllegalArgumentException("参数为空!");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
