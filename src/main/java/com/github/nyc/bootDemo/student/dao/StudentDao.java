package com.github.nyc.bootDemo.student.dao;

import org.apache.ibatis.annotations.Mapper;
import com.github.nyc.bootDemo.student.domain.Student;

@Mapper
public interface StudentDao {
	
	public void updateScore(Student student);
	
	public int updateScoreByVersion(Student student);
	
	public Student getStudentById(Integer id);
	
	public Student getByIdWithPessimisticLock(Integer id);

}
