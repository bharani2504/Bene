package com.example.bene.lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class BeneLock {

    static final Log LOG = LogFactory.getLog(BeneLock.class);
    private static LockRegistry lockRegistry;

    public BeneLock(LockRegistry lockRegistry) {
        BeneLock.lockRegistry = lockRegistry;
    }

    public static Lock obtainLock(String lockKey) {
        if (lockKey!=null) {
            return lockRegistry.obtain(lockKey);
        }
        return null;
    }

    public static Boolean tryLock(Lock lock) {
        try {

            if (lock != null) {
                return lock.tryLock();
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    public static void unlock(Lock lock) {
        try {

            if (lock != null) {
                lock.unlock();
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
