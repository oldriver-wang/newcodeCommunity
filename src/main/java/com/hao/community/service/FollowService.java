package com.hao.community.service;

import com.hao.community.entity.User;
import com.hao.community.util.CommunityConstant;
import com.hao.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;
    // 关注
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 某个用户关注的实体
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

                // 某个实体的粉丝
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);


                redisOperations.multi();

                redisOperations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return redisOperations.exec();
            }
        });
    }

    // 取关
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 某个用户关注的实体
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

                // 某个实体的粉丝
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);


                redisOperations.multi();

                redisOperations.opsForZSet().remove(followeeKey, entityId);
                redisOperations.opsForZSet().remove(followerKey, userId);
                return redisOperations.exec();
            }
        });
    }

    // 查询某个目标关注的实体的数量  每一种实体一份
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }
    // 查询某个实体的粉丝数量
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }


    // 查询当前用户是否已经关注该实体
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        // 若查到的值不是空的说明已关注
        return redisTemplate.opsForZSet().score(followKey, entityId)!=null;
    }

    // 查询某用户关注的人
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        // 查到的 是 offset 到 offset + limit - 1 包含[,]
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if(targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    // 查询某用户的粉丝
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        // 返回 Redis自己实现的内置类
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset+limit-1);
        if(targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }


}
