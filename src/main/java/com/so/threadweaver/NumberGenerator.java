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
                    String a = min + "" + counter.incrementAndGet();
                    System.out.println(a);
                    return a;
                } else {
                    counter.set(0);
                    String a = timeMachine.getCurrentMinute() + "" + counter.incrementAndGet();
                    System.out.println(a);
                    return a;
                }
            } finally {
                lock.set(false);
            }
        }
        String a = min + "" + counter.incrementAndGet();
        System.out.println(a);
        return a;
    }
}