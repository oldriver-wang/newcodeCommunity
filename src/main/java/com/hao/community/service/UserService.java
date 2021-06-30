package com.hao.community.service;

import com.hao.community.dao.UserMapper;
import com.hao.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    public User findUserById(int id) {
        return userMapper.selectById(id);

        // 为外键显示用户名称
    }
}
