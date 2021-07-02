package com.hao.community.dao;

import com.hao.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;


/**
 * 实现mapper的两种方法
 * 1、xml文件
 * 2、注解
 */

@Mapper
public interface LoginTicketMapper {

    // 插入一条数据
    // 多个字符串拼接成一个sql  {"", "", ""}
    // 主键自动生成  Option 注解
    // 这儿走的跟application.properties 不一样
    @Insert({"insert into login_ticket (user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})",
            ";"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);


    // 依据是ticket  主要是根据ticket来查询
    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket=#{ticket} ",
            ";"
    })
    LoginTicket selectByTicket(String ticket);


    /**
     *     @Update({
     *             "update login_ticket set status=#{status} where ticket = #{ticket} ",
     *             ";"
     *     })

     * @Update({
     *     如何写动态sql 在 注解上
     *     "<script>",
     *     "update login_ticket set status=#{status} where ticket=#{ticket} ",
     *     "<if test=\"ticket!=null\">",
     *     "and 1=1",
     *     "</if>",
     *     "<script/>"
     *     })
     */


    // 退出时修改状态 而不是删除  为了后边统计




    @Update({
            "update login_ticket set status=#{status} where ticket = #{ticket} ",
            ";"
    })
    int updateStatus(String ticket, int status);
}
