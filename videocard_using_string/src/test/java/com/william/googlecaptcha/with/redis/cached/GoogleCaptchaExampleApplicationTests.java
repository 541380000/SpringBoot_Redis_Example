package com.william.googlecaptcha.with.redis.cached;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class GoogleCaptchaExampleApplicationTests {

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	RedisTemplate redisTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void testRedisConnection() {
		ValueOperations ops = stringRedisTemplate.opsForValue();
		ops.set("test:test:test1", "TestString");
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String res = (String) ops.get("test:test:test1");
		stringRedisTemplate.opsForValue().getOperations().delete("test:test:test1");
		System.out.println(res);
	}

}
