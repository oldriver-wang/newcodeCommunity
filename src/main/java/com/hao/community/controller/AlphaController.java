package com.hao.community.controller;


import com.hao.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot";
    }



    // 通过response对象可以向浏览器输出任何东西， 加以声明
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        // request 常用接口
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());

        Enumeration<String> enumeration = request.getHeaderNames();
        // 获取参数

        while(enumeration.hasMoreElements()) {

            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            // 获取参数名称
            System.out.println(name + ":" +value);
        }
        System.out.println(request.getParameter("code"));

        // 向浏览器返回响应数据
        response.setContentType("text/html;charset=utf-8");
        // java7新语法  在try后面小括号写 writer   自动释放   不用加finally
        try(PrintWriter writer = response.getWriter();) {

            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // GET请求
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name="current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false,defaultValue = "10") int limit) {
        // 参数名和get
        System.out.println(current);
        System.out.println(limit);
        // 使用RequestParam注解对参数进行更详尽的说明
        return "some students";
    }


    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    @RequestMapping(path="/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {
        System.out.println(name+age);
        return "success";
    }

    @RequestMapping(path="/teacher", method=RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "张三");
        modelAndView.addObject("age", 30);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping(path="/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        // 识别到参数有model对象, Spring会自动给你传入 而view 直接返回  上面的是在内部设置好了再返回
        // 方式不一样结果差不多

        model.addAttribute("name", "北京大学");
        model.addAttribute("age", 80);
        return "/demo/view";
    }

    // 响应html的两种方法

    // 响应json数据 (异步请求)  当前网页不刷新   返回判断结果
    // java对象  -> JSON 字符串  -> js对象
    @RequestMapping(path="/emp", method=RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmp() {
        List<Map<String, Object>> emps = new ArrayList<>();

        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);


        emps.add(emp);

        emp = new HashMap<>();
        emp.put("name", "李四");
        emp.put("age", 30);
        emp.put("salary", 10000.00);


        emps.add(emp);


        emp = new HashMap<>();
        emp.put("name", "王五");
        emp.put("age", 32);
        emp.put("salary", 12000.00);


        emps.add(emp);
        return emps;
        // 这里将会自动转为jsn 字符串   响应类型为json
    }

    @RequestMapping(path="/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());

        // 设置cookie生效的范围
        cookie.setPath("/community/alpha");

        // 设置cookie的生存时间
        cookie.setMaxAge(60 * 10);


        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    // 取参数code这个值
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }


    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");

        return "set session";
    }

    @RequestMapping(path="/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    @RequestMapping(path = "ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0, "ok");
    }
}
