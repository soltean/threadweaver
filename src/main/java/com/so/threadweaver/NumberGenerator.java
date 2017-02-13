package com.so.threadweaver;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberGenerator {

    private AtomicInteger counter = new AtomicInteger(0);
    private AtomicBoolean lock = new AtomicBoolean(false);

    private final TimeMachine timeMachine;

    public NumberGenerator(TimeMachine timeMachine) {
        this.timeMachine = timeMachine;
    }

    public String nextNumber() {
        int min = timeMachine.getCurrentMinute();
        if (lock.compareAndSet(false, true)) {
            try {
                if (timeMachine.getCurrentMinute() == min) {
                    return min + "" + counter.incrementAndGet();
                } else {
                    counter.set(0);
                    lock.set(false);
                    return timeMachine.getCurrentMinute() + "" + counter.incrementAndGet();
                }
            } finally {
                lock.set(false);
            }
        }
        return min + "" + counter.incrementAndGet();
    }
}