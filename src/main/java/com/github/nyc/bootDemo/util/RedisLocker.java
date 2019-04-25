package com.github.nyc.bootDemo.util;

import com.github.nyc.bootDemo.exception.UnableToAcquireLockException;
import com.github.nyc.bootDemo.support.AcquiredLockWorker;
import com.github.nyc.bootDemo.support.DistributedLocker;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * 基于redission 实现的分布式锁
* <p>Title: RedisLocker.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: cssweb.sh.cn</p>  
* @author nieyc  
* @date 2019年4月25日  
* @version 1.0
 */
@Component
public class RedisLocker implements DistributedLocker {

    private final static String LOCKER_PREFIX = "lock:";

    /**
     * The Redisson client.
     */
    @Autowired
    RedissonClient redissonClient;

    @Override
    public <T> T lock(String resourceName, AcquiredLockWorker<T> worker) throws Exception {
        return fairLock(resourceName, worker, 100);
    }

    @Override
    public <T> T tryLock(String resourceName, AcquiredLockWorker<T> worker, int lockTime) throws Exception {
        RLock lock = redissonClient.getLock(LOCKER_PREFIX + resourceName);
        // (可重入锁)最多等待100秒，锁定后经过lockTime秒后自动解锁
        boolean success = lock.tryLock(100, lockTime, TimeUnit.SECONDS);
        if (success) {
            try {
                return worker.invokeAfterLockAcquire();
            } finally {
                lock.unlock();
            }
        }
        throw new UnableToAcquireLockException();
    }

    @Override
    public <T> T fairLock(String resourceName, AcquiredLockWorker<T> worker, int lockTime) throws Exception {
        RLock lock = redissonClient.getFairLock(LOCKER_PREFIX + resourceName);
        // (公平锁)最多等待100秒，锁定后经过lockTime秒后自动解锁
        boolean success = lock.tryLock(100, lockTime, TimeUnit.SECONDS);
        if (success) {
            try {
                return worker.invokeAfterLockAcquire();
            } finally {
                lock.unlock();
            }
        }
        throw new UnableToAcquireLockException();
    }

}
