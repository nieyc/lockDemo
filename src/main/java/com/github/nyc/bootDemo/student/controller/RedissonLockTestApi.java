package com.github.nyc.bootDemo.student.controller;

import com.github.nyc.bootDemo.student.service.StudentService;
import com.github.nyc.bootDemo.support.AcquiredLockWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.nyc.bootDemo.util.RedisLocker;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@RestController
@Slf4j
public class RedissonLockTestApi {
    /**
     * The Distributed locker.
     */
    @Autowired
    RedisLocker distributedLocker;
    
    
	@Autowired
	StudentService studentService;

    /**
     * <p>并发下分布式锁测试API </p>  
     * @return
     * @throws Exception
     * @author nieyc 
     * @date 2019年4月25日
     */
    @RequestMapping(value = "/redlock")
    public String testRedlock() throws Exception {
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(5);
        // create and start threads
        for (int i = 0; i < 50; ++i) {
            new Thread(new Worker(startSignal, doneSignal)).start();
        }
        startSignal.countDown(); // let all threads proceed
        doneSignal.await();
        System.out.println("All processors done. Shutdown connection");
        return "redlock";
    }

    /**
    * <p>Title: RedissonLockTestApi.java</p>  
    * <p>Description: </p>  
    * <p>Copyright: Copyright (c) 2019</p>  
    * <p>Company: cssweb.sh.cn</p>  
    * @author nieyc  
    * @date 2019年4月25日  
    * @version 1.0
     */
    class Worker implements Runnable {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;

        /**
         * Instantiates a new Worker.
         *
         * @param startSignal the start signal
         * @param doneSignal  the done signal
         */
        Worker(CountDownLatch startSignal, CountDownLatch doneSignal) {
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
        }

        @Override
        public void run() {
            try {
                startSignal.await();
                //尝试加锁
                distributedLocker.lock("test", new AcquiredLockWorker<Object>() {
                    @Override
                    public Object invokeAfterLockAcquire() {
                        doTask();
                        return "success";
                    }
                });
            } catch (Exception e) {
                log.warn("获取锁出现异常", e);
            }
        }

        /**
         * Do task.
         */
        void doTask() {
            System.out.println(Thread.currentThread().getName() + " 抢到锁!");
            Random random = new Random();
            int _int = random.nextInt(200);
            System.out.println(Thread.currentThread().getName() + " sleep " + _int + "millis");
            try {
                Thread.sleep(_int);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 释放锁!");
            doneSignal.countDown();
        }
    }
}