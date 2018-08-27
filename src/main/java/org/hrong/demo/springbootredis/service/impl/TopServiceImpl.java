package org.hrong.demo.springbootredis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Service
public class TopServiceImpl {

	@Autowired
	private RedisTemplate redisTemplate;
	//三分钟过期，便于测试
	private static final Long ARTICLES_EXPIRE = 180L;

	//删除redis中的数据
	public void del(Long id) {
		redisTemplate.delete("score");
		redisTemplate.delete("time");
		redisTemplate.delete("artical:" + id);
	}

	/**
	 * 投票流程：
	 * 先调用saveInfo2TimeZset判断文章是过了最后投票时间
	 * 根据键值判断是否存在用户重复投票
	 * 改变redis中对应文章的分值
	 * @param articalId
	 * @param userId
	 */
	@Transactional
	public void saveInfo(Long articalId, String userId) {
		System.out.println(userId + "开始投票给" + articalId + "号文章");
		boolean timeRes = saveInfo2TimeZset(articalId);
		if (timeRes) {
			boolean res = judgeInfoByUserInfo(articalId, userId);
			if (res) {
				saveInfo2ScoreZset(articalId);
			}
		}
	}

	//查看文章的投票者
	public void selectUserByArtical(Long articalId) {
		String key = "artical:" + articalId;
		Set members = redisTemplate.opsForSet().members(key);
		System.out.println(articalId + "号文章的投票者:");
		for (Object member : members) {
			System.out.println(member);
		}
	}

	//发表文章
	public void publishArtical(Long articalId) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long currentTimeMillis = System.currentTimeMillis();
		String format = dateFormat.format(new Date(currentTimeMillis));
		String expire = dateFormat.format(new Date(currentTimeMillis + ARTICLES_EXPIRE * 1000));
		redisTemplate.opsForZSet().add("time", articalId, currentTimeMillis);
		redisTemplate.opsForZSet().add("score", articalId, 0);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(articalId+"号文章发表成功！发表时间：" + format + ",过期时间：" + expire);
	}

	//判断是否重复投票，如果不存在重复投票则写入数据
	public boolean judgeInfoByUserInfo(Long articalId, String userId) {
		String key = "artical:" + articalId;
		Long add = redisTemplate.opsForSet().add(key, userId);
		if (add == 0) {
			System.out.println(userId+"已经为"+articalId+"号文章投票，不能重复投票");
			return false;
		} else {
			return true;
		}
	}

	//将数据尝试写入timeZset，超过时间不能进行投票
	public boolean saveInfo2TimeZset(Long articalId) {
		Double time = redisTemplate.opsForZSet().score("time", articalId);
		long now = System.currentTimeMillis();
		if (time + ARTICLES_EXPIRE * 1000 < now) {
			System.out.println("文章超过截止投票时间！");
			return false;
		}
		return true;
	}


	//对分值zset进行添加数据操作，每次加一分
	public void saveInfo2ScoreZset(Long articalId) {
		Double start = redisTemplate.opsForZSet().score("score", articalId);
		Double score = redisTemplate.opsForZSet().incrementScore("score", articalId, 1.0);
		System.out.println("为" + articalId + "号文章投票成功，投票前积分为:" + start + ",投票后积分为：" + score);
	}


	//显示结果
	public void selectArticalsByTimeAndScore() {
		Set set = redisTemplate.opsForZSet().reverseRange("time", 0L, -1L);
		System.out.println("按时间排序的结果：");
		for (Object o : set) {
			System.out.print(o);
			System.out.println("    "+redisTemplate.opsForZSet().score("time",o));
		}
		Set set1 = redisTemplate.opsForZSet().reverseRange("score", 0L, -1L);
		System.out.println("按分值排序的结果：");
		for (Object o : set1) {
			System.out.print(o);
			System.out.println("    "+redisTemplate.opsForZSet().score("score",o));
		}

	}


}
