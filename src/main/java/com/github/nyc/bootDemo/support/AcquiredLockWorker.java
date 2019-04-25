package com.github.nyc.bootDemo.support;

public interface AcquiredLockWorker<T> {
    /**
     * Invoke after lock aquire t.
     *
     * @return the t
     * @throws Exception the exception
     */
    T invokeAfterLockAcquire() throws Exception;
}
