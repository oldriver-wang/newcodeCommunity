package com.hao.community.service;


import com.hao.community.dao.DiscussPostMapper;
import com.hao.community.entity.DiscussesPost;
import com.hao.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.swing.text.html.HTML;
import java.util.List;


// 服务层组件
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussesPost> findDiscussesPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int countDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostsRows(userId);
    }

    public int addDiscussPost(DiscussesPost discussesPost) {
        if (discussesPost == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        System.out.println(discussesPost);

        discussesPost.setTitle(HtmlUtils.htmlEscape(discussesPost.getTitle()));
        discussesPost.setContent(HtmlUtils.htmlEscape(discussesPost.getContent()));

        discussesPost.setTitle(sensitiveFilter.filter(discussesPost.getTitle()));
        discussesPost.setContent(sensitiveFilter.filter(discussesPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussesPost);
    }


    public DiscussesPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }


    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }
}
