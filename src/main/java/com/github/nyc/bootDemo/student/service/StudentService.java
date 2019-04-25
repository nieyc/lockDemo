package com.github.nyc.bootDemo.student.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Resource;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.nyc.bootDemo.student.controller.RedissonLockTestApi;
import com.github.nyc.bootDemo.student.dao.StudentDao;
import com.github.nyc.bootDemo.student.domain.Student;
import com.github.nyc.bootDemo.support.AcquiredLockWorker;
import com.github.nyc.bootDemo.util.DistributedLock;
import com.github.nyc.bootDemo.util.RedisLocker;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StudentService {
	 Lock lock=new ReentrantLock();
	 
	 DistributedLock dlock=new DistributedLock("zktest");
	 
	 @Autowired
	 RedisLocker distributedLocker;
	 
	 @Autowired
	 RedissonClient redissonClient;
	 
	 @Resource
	 private StudentDao studentDao;
	 
	 /**
	  * 
	  * <p>没有锁机制 </p>  
	  * @param student
	  * @author nieyc 
	  * @date 2019年4月15日
	  */
	 @Transactional(rollbackFor = RuntimeException.class)
	 public  void updateScore(Student student) {
			 student=studentDao.getStudentById(student.getId());
			 if(student.getScore()>0) {
				 studentDao.updateScore(student);
			 }else {
				System.out.println("score is less than 0");
			 }
	 }
	 
	 /**
	  * <p>同步锁 </p>  
	  * @param student
	  * @author nieyc 
	  * @date 2019年4月12日
	  */
	 public  void testSyncLockUpdateScore(Student student) {
		 synchronized (this) {
			 student=studentDao.getStudentById(student.getId());
			 if(student.getScore()>0) {
				 studentDao.updateScore(student);
			 }else {
				System.out.println("score is less than 10");
			 }
		}
	 }
	 
	 /**
	  * 
	  * <p>ReentrantLock </p>  
	  * @param student
	  * @author nieyc 
	  * @date 2019年4月12日
	  */
	 public  void testLockScore(Student student) {
		     lock.lock();
			 student=studentDao.getStudentById(student.getId());
			 if(student.getScore()>0) {
				 studentDao.updateScore(student);
			 }else {
				System.out.println("score is less than 10");
			 }
			 lock.unlock();
	 }
	 
	 
	 /**
	  * <p>基于数据库的乐观锁，在分布式环境可以满足业务，实现并不依赖数据库，而是开发者自己写逻辑实现，一般基于版本号和时间戳实现</p>  
	  * @param student
	  * @author nieyc 
	  * @date 2019年4月15日
	  */
	 public  int testOptimisticLockScore(Student student) {
		 student=studentDao.getStudentById(student.getId());
		 int i=0;
		 if(student.getScore()>0) {
			 i=studentDao.updateScoreByVersion(student);
			 if(i==0) {
				 System.out.println("更新记录失败。。。。。。。。。。。。。。。。。。。。。。。。。。。:"+i);
			 }else {
				 System.out.println("更新记录成功************************************："+i);
			 }
		 }else {
			System.out.println("score is less than 10");
		 }
		 return i;
     }
	 
	 
	 /**
	  * <p>依赖于数据库机制的悲观锁实现 ，分布式环境中可以满足业务</p> 
	  * @param student
	  * @author nieyc 
	  * @date 2019年4月15日
	  */
	 @Transactional(rollbackFor = RuntimeException.class)
	 public void testPessimisticLock(Student student) {
		 student=studentDao.getByIdWithPessimisticLock(student.getId());
		 if(student.getScore()>0) {
			  studentDao.updateScore(student); 
		 }else {
		     System.out.println("score is less than 10");
		 }
	 }
	 
	 /**
	  * <p>基于zookeeper的分布式锁 </p>  
	  * @param student
	  * @author nieyc 
	  * @date 2019年4月17日
	  */
	 public void testZkLock(Student student) {
		 dlock.acquireLock();
		 try {
			 student=studentDao.getStudentById(student.getId());
			 if(student.getScore()>0) {
				  studentDao.updateScore(student); 
			 }else {
			     System.out.println("score is less than 10");
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dlock.releaseLock();
		}
	 }
	 
	 
	 public void testRedisLock(Student student) {
		 try {
			distributedLocker.lock("test", new AcquiredLockWorker<Object>() {
			       @Override
			       public Object invokeAfterLockAcquire() {
			    	   Student student1=studentDao.getStudentById(student.getId());
			  		 if(student1.getScore()>0) {
			  			  studentDao.updateScore(student1); 
			  		 }else {
			  			log.info("score is less than 0");
			  		 }
			  		  return "success";
			       }
			 });
		} catch (Exception e) {
			log.error("lock fail");
		}
	 }
	 
	 
	  public void testSimpleRedisLock(Student student) {
		 RLock lock = redissonClient.getLock("student");
		 try{
		    // 1. 最常见的使用方法
		    //lock.lock();
		    // 2. 支持过期解锁功能,10秒钟以后自动解锁, 无需调用unlock方法手动解锁
		    //lock.lock(10, TimeUnit.SECONDS);
		    // 3. 尝试加锁，最多等待2秒，上锁以后10秒自动解锁
		    boolean res = lock.tryLock(2, 10, TimeUnit.SECONDS);
		    if(res){ //成功
		    	 student=studentDao.getStudentById(student.getId());
		  		 if(student.getScore()>0) {
		  			  studentDao.updateScore(student); 
		  		 }else {
		  			log.info("score is less than 0");
		  		 }
		    }
		 }catch (InterruptedException e) {
			    log.error(e.getMessage());
			}finally {
			    //释放锁
			   lock.unlock();
		}
       }
	 
	 
	 
	 public Student getStudentById(Integer id) {
		 return studentDao.getStudentById(id);
	 }

}
