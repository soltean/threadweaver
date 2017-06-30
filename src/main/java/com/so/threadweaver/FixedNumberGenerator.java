package com.so.threadweaver;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FixedNumberGenerator {

	private AtomicInteger counter = new AtomicInteger(0);
	private Lock reentrantLock = new ReentrantLock();
	private volatile boolean counterReset;

	private final TimeMachine timeMachine;

	public FixedNumberGenerator(TimeMachine timeMachine) {
		this.timeMachine = timeMachine;
	}

	public String nextNumber() {
		int min = timeMachine.getCurrentMinute();

		if (timeMachine.getCurrentMinute() == min) {
			return min + "" + counter.getAndIncrement();
		} else {
			try {
				reentrantLock.lock();
				if (!counterReset) {
					counter.set(0);
					counterReset = true;
				}
				return timeMachine.getCurrentMinute() + "" + counter.incrementAndGet();
			} finally {
				reentrantLock.unlock();
			}
		}
	}
}