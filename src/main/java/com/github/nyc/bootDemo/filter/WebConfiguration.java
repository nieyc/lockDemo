package com.github.nyc.bootDemo.filter;

import javax.servlet.Filter;

import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {
	
	    @Bean
	    public RemoteIpFilter remoteIpFilter() {
	        return new RemoteIpFilter();
	    }
	    
	    @Bean
	    public FilterRegistrationBean<Filter> testFilterRegistration() {

	        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
	        registration.setFilter(new MyFilter());
	        registration.addUrlPatterns("/*");
	        registration.addInitParameter("paramName", "paramValue");
	        registration.setName("MyFilter");
	        registration.setOrder(1);
	        return registration;
	    }

}
