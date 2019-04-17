package com.github.nyc.bootDemo.student.service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.nyc.bootDemo.student.dao.StudentDao;
import com.github.nyc.bootDemo.student.domain.Student;
import com.github.nyc.bootDemo.util.DistributedLock;

@Service
public class StudentService {
	 Lock lock=new ReentrantLock();
	 
	 DistributedLock dlock=new DistributedLock("zktest");
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
			 if(student.getScore()>=10) {
				 studentDao.updateScore(student);
			 }else {
				System.out.println("score is less than 10");
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
			 if(student.getScore()>=10) {
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
			 if(student.getScore()>=10) {
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
		 if(student.getScore()>=10) {
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
		 if(student.getScore()>=10) {
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
			 if(student.getScore()>=10) {
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
	 
	 
	 public Student getStudentById(Integer id) {
		 return studentDao.getStudentById(id);
	 }

}
