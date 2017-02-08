package com.so.threadweaver;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberGenerator {

    private volatile int currentMinute = LocalDateTime.now().getMinute();
    private AtomicInteger counter = new AtomicInteger(0);
    private AtomicBoolean lock = new AtomicBoolean();

    public String nextNumber1() {
        int min = LocalDateTime.now().getMinute();
        if (currentMinute == min) {
            return currentMinute + "" + counter.incrementAndGet();
        } else {
            if (lock.compareAndSet(false, true)) {
                counter.set(0);
                currentMinute = LocalDateTime.now().getMinute();
                String result = currentMinute + "" + counter.incrementAndGet();
                lock.set(false);
                return result;
            }
            return currentMinute + "" + counter.incrementAndGet();
        }
    }

    public String nextNumber() {
        int min = LocalDateTime.now().getMinute();
        if (currentMinute == min) {
            return currentMinute + "" + counter.incrementAndGet();
        } else {
            counter.set(0);
            currentMinute = LocalDateTime.now().getMinute();
            String result = currentMinute + "" + counter.incrementAndGet();
            return result;
        }
    }
}