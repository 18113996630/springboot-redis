package org.hrong.demo.springbootredis.service.impl;

import org.hrong.demo.springbootredis.service.IUserService;
import org.hrong.demo.springbootredis.utils.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private RedisLock redisLock;
	private static Map<String,Integer> stock;

	private static String key = "123";
	private static Long TIMEOUT = 90L;
	static {
		stock = new HashMap<>();
		stock.put("123",50);
	}

	@Override
	public <T> T saveUserByRedisTemplate() {
		long start = System.currentTimeMillis();
		Long value = System.currentTimeMillis()+TIMEOUT;
		boolean lock = redisLock.lock(key, String.valueOf(value));
		if (!lock) {
			System.out.println("*********************未获取到锁*************************");
			return (T)"未获取到锁";
		}
		Integer num = stock.get(key);
		if (num == 0) {
			System.out.println("*********************库存为0*************************");
			return (T)"数量为0";
		}
		stock.put(key, num-1);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("*********************下单成功剩余库存"+stock.get(key)+"*************************");
		long end = System.currentTimeMillis();
		System.out.println("花费时间:"+(end-start));
		redisLock.unlock(key,String.valueOf(value));
		return (T)("下单成功，剩余库存"+stock.get(key));
	}

	@Override
	public <T> T saveUserBystringRedisTemplate(T data) {
		if (stringRedisTemplate.hasKey("user")) {
			Boolean user = stringRedisTemplate.delete("user");
			return (T) ("已存在该键,先进行删除操作，删除结果："+user);
		}
		stringRedisTemplate.opsForValue().set("user",data.toString(),1L, TimeUnit.MINUTES);
		String user = stringRedisTemplate.opsForValue().get("user");
		return (T) user;
	}

	@Override
	public <T> T saveUserByIfAbsent(T data) throws InterruptedException {
//		if (redisTemplate.hasKey("key")) {
//			redisTemplate.opsForValue().setIfAbsent("key", "已经存在了");
//			return (T) redisTemplate.opsForValue().get("key");
//		}
//		redisTemplate.opsForValue().setIfAbsent("key", data);
//		return (T)redisTemplate.opsForValue().get("key");
//		if (redisTemplate.hasKey("key")) {
//			redisTemplate.delete("key");
//			return (T)("删除成功");
//		}
//		redisTemplate.opsForValue().setIfAbsent("key", data);
//		return (T)redisTemplate.opsForValue().get("key");
		if (redisTemplate.hasKey("key")) {
			redisTemplate.delete("key");
			redisTemplate.opsForValue().set("key","date",30,TimeUnit.SECONDS);
		}
		Thread.sleep(3000);
		Long time = redisTemplate.getExpire("key", TimeUnit.SECONDS);
		redisTemplate.opsForValue().getAndSet("key", time);
		return (T)(redisTemplate.getExpire("key", TimeUnit.SECONDS)+"-"+redisTemplate.opsForValue().get("key"));
	}
}
