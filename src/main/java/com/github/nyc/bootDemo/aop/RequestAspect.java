package com.github.nyc.bootDemo.aop;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Component
public class RequestAspect {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Pointcut("execution(public * com.github.nyc.bootDemo..*Controller.*(..))") 
	public void log(){
		
	}

	
	@Before("log()")
	public void doBefore(JoinPoint joinPoint){
		logger.info("方法执行前..."); 
		ServletRequestAttributes sra=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request=sra.getRequest(); 
		logger.info("url:"+request.getRequestURI()); 
		logger.info("ip:"+request.getRemoteHost()); 
		logger.info("method:"+request.getMethod()); 
		logger.info("class_method:"+joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName()); 
		logger.info("args:"+joinPoint.getArgs()); 
		
		}
	
	
	@After("log()")
	public void doAfter(JoinPoint joinPoint){
		logger.info("方法执行后...");
	} 
	
	@AfterReturning(returning="result",pointcut="log()") 
	public void doAfterReturning(Object result){
		logger.info("方法返回值："+result); 
		
	}


}
