package com.hao.community.dao;

import com.hao.community.entity.DiscussesPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface DiscussPostMapper {

    // 为今后个人主页加上userId
    // offset起始行行号  limit多少数据
    List<DiscussesPost> selectDiscussPosts(int userId, int offset, int limit);

    // 为了支持分页需要看一共有多少页  动态sql
    int selectDiscussPostsRows(@Param("userId") int userId);
    // Param 注解  1、起别名  当名称太长可以起别名
    // 如果只有一个参数，并且在<if> 里使用，则必须加别名
    // 当需要动态拼接一个条件，并且方法有且仅有一个条件，必须加别名
}
