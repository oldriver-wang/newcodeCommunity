package com.hao.community.controller;


import com.hao.community.annotation.LoginRequired;
import com.hao.community.entity.User;
import com.hao.community.service.FollowService;
import com.hao.community.service.LikeService;
import com.hao.community.service.UserService;
import com.hao.community.util.CommunityConstant;
import com.hao.community.util.CommunityUtil;
import com.hao.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path="/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null) {
            model.addAttribute("error", "还没有选择图片!");
            return "/site/setting";
        }

        // 需要保存文件  随机防止覆盖  并要先提取到后缀
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确");
            return "/site/setting";
        }

        // 随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 全限定名  确定文件的存放路径
        File dest = new File(uploadPath + "/" + fileName);

        try {
            // 存入文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败", e.getMessage());

            // 文件上传不了报异常信息
            throw new RuntimeException("上传文件失败， 服务器发生异常", e);
        }

        // 上传成功
        // 更新当前用户头像的路径 (web 访问路径)
        // http://localhost:8080/community/user/header/xxx.png

        User user = hostHolder.getUser();
        String headUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headUrl);

        return "redirect:/index";
    }
    // 返回是二值数据

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放的路径
        fileName = uploadPath + '/' + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应
        response.setContentType("image/"+suffix);
        try (
                // 输出流是Springmvc创建的 而 输入流是我们自己创建的需要自己关闭
                FileInputStream fis = new FileInputStream(fileName);
                ServletOutputStream os = response.getOutputStream();
             ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b=fis.read(buffer))!=-1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败", e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/changePassword", method = RequestMethod.POST)
    public String changePassword(String oldPassword, String newPassword, Model model){
        User user = hostHolder.getUser();

        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword);
        if(map.containsKey("passwordMsg")){
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "redirect:/setting";
        }

        return "redirect:/logout";
    }

    // @LoginRequired
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        // 用户
        model.addAttribute("user", user);
        // 点赞数
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);



        // 那个页面需要显示就在那个页面添加
        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);


        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        boolean hasFollowed = false;
        //是否已关注
        if(hostHolder.getUser()!=null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }



}
