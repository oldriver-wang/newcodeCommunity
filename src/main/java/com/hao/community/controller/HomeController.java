package com.hao.community.controller;

import com.hao.community.entity.DiscussesPost;
import com.hao.community.entity.Page;
import com.hao.community.entity.User;
import com.hao.community.service.DiscussPostService;
import com.hao.community.service.LikeService;
import com.hao.community.service.UserService;
import com.hao.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostServiceImpl;


    @Autowired
    private UserService userService;

    // 补充点赞逻辑
    @Autowired
    private LikeService likeService;


    // 可以return modelandview
    // 也可以返回字符串， 代表view的名字
    @RequestMapping(path="/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        // 方法调用之前， SpringMVC会自动实例化Model和Page， 并将Page注入Model
        // 所以，在thymeleaf可以直接访问Page对象中的数据
        page.setRows(discussPostServiceImpl.countDiscussPostRows(0));
        page.setPath("/index");
        // 当前页码，  不需要服务器计算

        // List<DiscussesPost> list = discussPostServiceImpl.findDiscussesPosts(0, 0, 10);
        // 使用page里传入的信息
        List<DiscussesPost> list = discussPostServiceImpl.findDiscussesPosts(0, page.getOffset(), page.getLimit());

        List<Map<String, Object>> discussesPosts = new ArrayList<>();
        if(list != null){
            for(DiscussesPost post : list){
                Map<String, Object> map = new HashMap<>();
                // System.out.println(post);
                map.put("post", post);
                userService.findUserById(post.getUserId());
                User user = userService.findUserById(post.getUserId());
                System.out.println(user);
                map.put("user", user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);


                discussesPosts.add(map);

            }
        }
        model.addAttribute("discussPosts", discussesPosts);
        return "/index";
    }

}
