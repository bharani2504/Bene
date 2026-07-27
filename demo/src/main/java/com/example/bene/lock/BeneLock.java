package com.example.bene.lock;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class BeneLock {

    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public ReentrantLock getLock(String key) {
        return lockMap.computeIfAbsent(key, k -> new ReentrantLock());
    }

    public void removeLockIfUnused(String key) {
        ReentrantLock lock = lockMap.get(key);
        if (lock != null && !lock.isLocked() && !lock.hasQueuedThreads()) {
            lockMap.remove(key, lock);
        }
    }
}
