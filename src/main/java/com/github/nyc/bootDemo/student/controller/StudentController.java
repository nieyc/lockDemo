package com.github.nyc.bootDemo.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.nyc.bootDemo.student.domain.Student;
import com.github.nyc.bootDemo.student.service.StudentService;


/**
* <p>Title: StudentController.java</p>  
* <p>Description: 各种锁的实现</p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: cssweb.sh.cn</p>  
* @author nieyc  
* @date 2019年4月18日  
* @version 1.0
 */
@RestController
@RequestMapping("/student")
public class StudentController {

	@Autowired
	StudentService studentService;
	
	@RequestMapping("/getStudentFromDb")
    public Student getUser() {
        Student student=studentService.getStudentById(2);
        return student;
    }
	
	
	@RequestMapping("/updateScore")
    public Student updateScore() {
	    Student student=new Student();
	    student.setId(2);
        studentService.updateScore(student);
        student=studentService.getStudentById(2);
        return student;
    }
	
	@RequestMapping("/updateLockScore")
    public Student updateLockScore() {
	    Student student=new Student();
	    student.setId(2);
        studentService.testSyncLockUpdateScore(student);
        student=studentService.getStudentById(2);
        return student;
    }
	
	
	@RequestMapping("/updateOptimisticLockScore")
    public Student updateDbLockScore() {
	    Student student=new Student();
	    student.setId(2);
        studentService.testOptimisticLockScore(student);
        student=studentService.getStudentById(2);
        return student;
    }
	
	
	@RequestMapping("/updateDbPessimisticScore")
    public Student updateDbPessimisticScore() {
	    Student student=new Student();
	    student.setId(2);
        studentService.testPessimisticLock(student);
        student=studentService.getStudentById(2);
        return student;
    }
	
	@RequestMapping("/updateZkScore")
    public Student updateZkScore() {
	    Student student=new Student();
	    student.setId(2);
        studentService.testZkLock(student);
        student=studentService.getStudentById(2);
        return student;
    }
	
	/**
	 * 
	 * <p>redission实现的分布式锁 </p>  
	 * @return
	 * @author nieyc 
	 * @date 2019年4月25日
	 */
	@RequestMapping("/updateRedisScore")
    public Student updateRedisScore() {
	    Student student=new Student();
	    student.setId(2);
        studentService.testRedisLock(student);
        student=studentService.getStudentById(2);
        return student;
    }
	
	
	/**
	 * <p>redission 简单实现 </p>  
	 * @return
	 * @author nieyc 
	 * @date 2019年4月25日
	 */
	@RequestMapping("/updateRedisScore1")
    public Student updateRedissionScore() {
	    Student student=new Student();
	    student.setId(2);
        studentService.testSimpleRedisLock(student);
        student=studentService.getStudentById(2);
        return student;
    }
}
