package com.hao.community;


import com.hao.community.dao.DiscussPostMapper;
import com.hao.community.dao.LoginTicketMapper;
import com.hao.community.dao.MessageMapper;
import com.hao.community.dao.UserMapper;
import com.hao.community.entity.DiscussesPost;
import com.hao.community.entity.LoginTicket;
import com.hao.community.entity.Message;
import com.hao.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);



        user = userMapper.selectByUsername("liubei");
        System.out.println(user);


        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }


    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }


    @Test
    public void testUpdateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }

    @Test
    public void testSelectDiscussPosts() {
        List<DiscussesPost> discussesPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for(DiscussesPost post: discussesPosts){
            System.out.println(post);
        }

        System.out.println(discussPostMapper.selectDiscussPostsRows(0));
        System.out.println(discussPostMapper.selectDiscussPostsRows(149));
    }


    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        int res = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(res);
    }



    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdateLoginTicket() {
        int status = 1;
        String ticket = "abc";
        loginTicketMapper.updateStatus(ticket, status);
    }

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectLetters() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        List<Message> messages1 = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : messages1) {
            System.out.println(message);
        }

        int count1 = messageMapper.selectLetterCount("111_112");
        System.out.println(count1);

        int count2 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count2);
    }
}
