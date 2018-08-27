package org.hrong.demo.springbootredis.controller;

import org.hrong.demo.springbootredis.pojo.User;
import org.hrong.demo.springbootredis.service.IUserService;
import org.hrong.demo.springbootredis.utils.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	@Autowired
	private IUserService userServiceImpl;


	@RequestMapping(value = "/user/RedisTemplate",method = RequestMethod.GET)
	public Object saveUserByRedisTemplate(){
		return userServiceImpl.saveUserByRedisTemplate();
	}
	@RequestMapping(value = "/user/BystringRedisTemplate",method = RequestMethod.POST)
	public Object saveUserBystringRedisTemplate(@RequestBody User user){
		return userServiceImpl.saveUserBystringRedisTemplate(user);
	}
	@RequestMapping(value = "/user/1",method = RequestMethod.POST)
	public Object saveUserByIfAbsent(@RequestBody User user) throws InterruptedException {
		return userServiceImpl.saveUserByIfAbsent(user);
	}

}
