package com.example.demo.service.impl;

import com.example.demo.dao.UserMapper;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }

    @Override
    public Integer delete(Integer id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public User getUserById(Integer id) {
        String key = "user_" + id;

        ValueOperations<String,User> operations = redisTemplate.opsForValue();

        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey){
            User user = operations.get(key);
            System.out.println("从redis缓存中获取");
            System.out.println(user.getUsername());
            return user;
        }else {
            User user = userMapper.selectByPrimaryKey(id);
            System.out.println("从数据表中获取");
            System.out.println(user.getUsername());
            operations.set(key, user,5, TimeUnit.HOURS);
            return user;
        }
    }
}
