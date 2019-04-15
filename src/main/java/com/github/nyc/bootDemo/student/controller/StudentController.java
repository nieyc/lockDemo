package com.github.nyc.bootDemo.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.nyc.bootDemo.student.domain.Student;
import com.github.nyc.bootDemo.student.service.StudentService;

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
}
