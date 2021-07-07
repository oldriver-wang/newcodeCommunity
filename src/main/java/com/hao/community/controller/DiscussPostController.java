package com.hao.community.controller;



import com.hao.community.entity.Comment;
import com.hao.community.entity.DiscussesPost;
import com.hao.community.entity.Page;
import com.hao.community.entity.User;
import com.hao.community.service.CommentService;
import com.hao.community.service.DiscussPostService;
import com.hao.community.service.LikeService;
import com.hao.community.service.UserService;
import com.hao.community.util.CommunityConstant;
import com.hao.community.util.CommunityUtil;
import com.hao.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;


    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "用户未登录!");
        }
        DiscussesPost discussesPost = new DiscussesPost();
        discussesPost.setUserId(user.getId());
        discussesPost.setTitle(title);
        discussesPost.setContent(content);
        discussesPost.setCreateTime(new Date());
        System.out.println(discussesPost);

        discussPostService.addDiscussPost(discussesPost);
        System.out.println("发布成功");

        // 报错的情况，稍后统一处理
        return CommunityUtil.getJSONString(0, "发布成功");
    }


    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        //帖子
        DiscussesPost discussesPost = discussPostService.findDiscussPostById(discussPostId);
        User user = userService.findUserById(discussesPost.getUserId());


        // 点赞数
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        // 若用户没登录这个值就取不到了
        int likeStatus = hostHolder.getUser()==null? 0:
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);

        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);

        model.addAttribute("user", user);
        model.addAttribute("post", discussesPost);

        // 评论的分页信息
        page.setLimit(5);
        // 设置路径
        page.setPath("/discuss/detail/" + discussPostId);

        // 总数
        page.setRows(discussesPost.getCommentCount());


        List<Comment> commentList= commentService.findCommentsByEntity(ENTITY_TYPE_POST,
                discussesPost.getId(), page.getOffset(), page.getLimit());



        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        // 评论VO列表
        if(commentList != null) {
            for(Comment comment: commentList) {
                // 评论VO  viewObject
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                // 回复列表
                // 有多少条查多少条
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,
                        comment.getId(), 0, Integer.MAX_VALUE);

                // 点赞数
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);

                // 若用户没登录这个值就取不到了
                likeStatus = hostHolder.getUser()==null? 0:
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);

                // 回复的VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null) {
                    for(Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));

                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        // 点赞数
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);

                        // 若用户没登录这个值就取不到了
                        likeStatus = hostHolder.getUser()==null? 0:
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);


                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                int replyCount = commentService.countComments(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }

}
