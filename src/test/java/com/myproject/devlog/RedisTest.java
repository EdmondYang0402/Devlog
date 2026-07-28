package com.myproject.devlog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testSetAndGet() {
        String key = "devlog:test:name";
        String value = "Edmond";

        stringRedisTemplate.opsForValue().set(key, value);

        String result = stringRedisTemplate.opsForValue().get(key);

        System.out.println("Redis读取结果：" + result);
        assertEquals(value, result);
    }

    @Test
    void testSetWithTtl() {
        String key = "devlog:test:code";

        stringRedisTemplate.opsForValue().set(
                key,
                "123456",
                Duration.ofSeconds(60)
        );

        String value = stringRedisTemplate.opsForValue().get(key);
        Long ttl = stringRedisTemplate.getExpire(key);

        System.out.println("value = " + value);
        System.out.println("剩余TTL = " + ttl + "秒");

        assertEquals("123456", value);
        assertNotNull(ttl);
        assertTrue(ttl > 0);
    }

    @Test
    void testHasKey() {
        String key = "devlog:test:session";

        stringRedisTemplate.opsForValue().set(
                key,
                "user:1",
                Duration.ofMinutes(5)
        );

        Boolean exists = stringRedisTemplate.hasKey(key);

        System.out.println("Key是否存在：" + exists);

        assertTrue(Boolean.TRUE.equals(exists));
    }

    @Test
    void testDelete() {
        String key = "devlog:test:logout";

        stringRedisTemplate.opsForValue().set(
                key,
                "user:1",
                Duration.ofMinutes(5)
        );

        assertTrue(Boolean.TRUE.equals(
                stringRedisTemplate.hasKey(key)
        ));

        Boolean deleted = stringRedisTemplate.delete(key);

        System.out.println("是否删除成功：" + deleted);

        assertFalse(Boolean.TRUE.equals(
                stringRedisTemplate.hasKey(key)
        ));
    }
}