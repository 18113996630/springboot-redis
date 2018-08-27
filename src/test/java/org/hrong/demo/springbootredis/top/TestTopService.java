package org.hrong.demo.springbootredis.top;

import org.hrong.demo.springbootredis.SpringbootRedisApplication;
import org.hrong.demo.springbootredis.service.impl.TopServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = {TestTopService.class, SpringbootRedisApplication.class})
@RunWith(SpringRunner.class)
public class TestTopService {

	@Autowired
	private TopServiceImpl topServiceImpl;


	@Test
	public void testDelete() {
		for (int i = 0; i < 5; i++) {
			topServiceImpl.del((long) i);
		}
	}

	@Test
	public void testPublish() {
		topServiceImpl.publishArtical(2L);
		topServiceImpl.publishArtical(3L);
		topServiceImpl.publishArtical(4L);
		topServiceImpl.publishArtical(1L);
	}

	@Test
	public void testSelect() {
		for (int i = 1; i < 5; i++) {
			topServiceImpl.selectUserByArtical((long) i);
		}
		topServiceImpl.selectArticalsByTimeAndScore();
	}

	@Test
	public void testFlow() {
		for (int i = 100; i < 103; i++) {
			topServiceImpl.saveInfo(2L,"user"+i);
		}
		for (int i = 100; i < 102; i++) {
			topServiceImpl.saveInfo(1L,"user"+i);
		}
		topServiceImpl.saveInfo(3L,"user100");
		topServiceImpl.saveInfo(4L, "user100");
	}

	@Test
	public void testSaveInfo() {
		topServiceImpl.publishArtical(4L);
		topServiceImpl.saveInfo(4L,"user99");
	}


}
