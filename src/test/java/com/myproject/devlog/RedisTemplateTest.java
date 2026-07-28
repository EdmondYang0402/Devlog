package com.myproject.devlog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@DataRedisTest
public class RedisTemplateTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate; // 操作字符串（默认String序列化）


    // 所有练习方法都将写在这里
    @Test
    public void testStringOperations() {
        String key = "name";
        String value = "Alice";
        stringRedisTemplate.opsForValue().set(key,value);
        String rs=stringRedisTemplate.opsForValue().get(key);
        System.out.println(rs);
    }

}
