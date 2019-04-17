package com.github.nyc.bootDemo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.github.nyc.bootDemo.student.domain.Student;
import com.github.nyc.bootDemo.student.service.StudentService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentTest {
	
	@Autowired
	StudentService studentService;
	
	/**
	 *   测试目的：多线程并发下各种锁表现：
	 *  1：不加锁情况，score最终分数为负数
	 *  2：加同步锁机制(基于单机jvm可以保证score最终为0)，分布式环境下出错(nginx+2个tomcat)，score结果小于0，分布式环境基于网络
	   *             压测工具：apacheab（总执行100次，并发10）
	 *           ab -n 100 -c 10 http://172.16.2.149/student/updateLockScore
	   *    分布式锁---基于数据库实现(悲观锁和乐观锁)
	 * @throws InterruptedException
	 * @author nieyc 
	 * @date 2019年4月12日
	 */
	@Test
	public void TestUpdateScore() throws InterruptedException {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 100, 10, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(200), new ThreadPoolExecutor.DiscardOldestPolicy());

		for (int i = 0; i < 100; i++) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					Student student = new Student();
					student.setId(2);
					//studentService.updateScore(student);
					//studentService.testOptimisticLockScore(student);
					//studentService.testPessimisticLock(student);
					studentService.testZkLock(student);
				}
			});
		}

		executor.shutdown();
		Thread.sleep(1000000);
	}
	
	

	
	

	

}
