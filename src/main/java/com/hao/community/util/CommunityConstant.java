package com.hao.community.util;

public interface CommunityConstant {
    // 激活成功
    int ACTIVATION_SUCCESS = 0;
    // 重复激活
    int ACTIVATION_REPEAT = 1;
    // 激活失败
    int ACTIVATION_FAILURE= 2;



    // 默认状态 登录超时时间
    // 不勾记住我我要存多久
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    // 勾上记住我我要存多久
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    // 设置实体评论的目标类型
    // POST
    int ENTITY_TYPE_POST = 1;
    // COMMENT
    int ENTITY_TYPE_COMMENT = 2;
    // USER
    int ENTITY_TYPE_USER = 3;


}
