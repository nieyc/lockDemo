package com.github.nyc.bootDemo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfiguration {
	/**
     * Gets client.
     * https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95  配置
     * @return the client
     */
    @Bean
    public RedissonClient redissonClient() {
    	Config config = new Config();
    	config.useClusterServers()
        .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
        .addNodeAddress("redis://172.16.2.55:6000", "redis://172.16.2.55:7000","redis://172.16.2.55:8000")
        .addNodeAddress("redis://172.16.2.56:6000", "redis://172.16.2.56:7000","redis://172.16.2.56:8000");
    	RedissonClient client=Redisson.create(config);
        return client;
    }
}
