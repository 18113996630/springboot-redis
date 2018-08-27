package org.hrong.demo.springbootredis.service;

import org.hrong.demo.springbootredis.pojo.User;

public interface IUserService {
	<T> T saveUserByRedisTemplate();

	<T> T saveUserBystringRedisTemplate(T data);

	<T> T saveUserByIfAbsent(T data) throws InterruptedException;
}
