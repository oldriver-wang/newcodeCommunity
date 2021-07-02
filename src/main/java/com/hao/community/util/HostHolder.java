package com.hao.community.util;


import com.hao.community.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    // 起到一个容器的作用
    // 持有用户信息  由于代替session对象
    // 保持线程之间隔离互不影响  每个线程得到的不一样 以线程为key存取值
    private ThreadLocal<User> users = new ThreadLocal<User>();

    public void setUsers(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
