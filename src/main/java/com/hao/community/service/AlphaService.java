package com.hao.community.service;

import com.hao.community.dao.DiscussPostMapper;
import com.hao.community.dao.UserMapper;
import com.hao.community.entity.DiscussesPost;
import com.hao.community.entity.User;
import com.hao.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
public class AlphaService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    
    @Autowired
    private TransactionTemplate transactionTemplate;

    @PostConstruct
    public void init() {
        System.out.println("初始化AlphaService");
    }

    public void destroy() {
        System.out.println("销毁AlphaService");
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setStatus(0);
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("aplha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        DiscussesPost post = new DiscussesPost();
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);


        Integer.valueOf("abc");
        return "ok";
    }
    
    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionTemplate.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);

        // 用Template底层自动调用
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setStatus(0);
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                DiscussesPost post = new DiscussesPost();
                post.setUserId(user.getId());
                post.setTitle("nihao");
                post.setContent("新人报道！");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);


                Integer.valueOf("abc");
                return "ok";
            }
        });
    }
}
