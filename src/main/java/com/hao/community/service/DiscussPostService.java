package com.hao.community.service;


import com.hao.community.dao.DiscussPostMapper;
import com.hao.community.entity.DiscussesPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


// 服务层组件
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussesPost> findDiscussesPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int countDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostsRows(userId);
    }
}
