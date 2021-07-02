package com.hao.community.config;


import com.hao.community.controller.interceptor.AlphaInterceptor;
import com.hao.community.controller.interceptor.LoginRequiredInterceptor;
import com.hao.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


// 声明一个第三方的Bean
// 配置拦截器
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    // 所有的css都排除
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.js")
                .addPathPatterns("/register", "/login");
        // 添加登录判断拦截器
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.js");
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.js");
    }
}
