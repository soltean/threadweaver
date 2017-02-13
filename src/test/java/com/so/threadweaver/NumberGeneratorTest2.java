package com.so.threadweaver;

import com.google.testing.threadtester.Breakpoint;
import com.google.testing.threadtester.ClassInstrumentation;
import com.google.testing.threadtester.CodePosition;
import com.google.testing.threadtester.Instrumentation;
import com.google.testing.threadtester.ObjectInstrumentation;
import com.google.testing.threadtester.ThreadedTest;
import com.google.testing.threadtester.ThreadedTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NumberGeneratorTest2 {

    private String first;
    private String second;
    private NumberGenerator generator;

    ThreadedTestRunner runner = new ThreadedTestRunner();

    @Test
    public void testThreadedTests() {
        runner.runTests(this.getClass(), NumberGenerator.class);
    }

    @ThreadedTest
    public void testGenerator() throws Exception {
        TimeMachine timeMachine = MockedTimeMachine.getTimeMachineMock();
        generator = new NumberGenerator(timeMachine);
        Runnable task = () -> first = generator.nextNumber();
        Thread thread1 = new Thread(task);
        ObjectInstrumentation<NumberGenerator> instrumented = Instrumentation.getObjectInstrumentation(generator);
        Breakpoint breakpoint1 = getBreakPoint(instrumented, thread1);
        thread1.start();
        breakpoint1.await();
        second = generator.nextNumber();
        breakpoint1.resume();
        thread1.join();
        Assertions.assertThat(first).isNotEqualTo(second);
    }

    private Breakpoint getBreakPoint(ObjectInstrumentation instrumentation, Thread thread) throws NoSuchMethodException, NoSuchFieldException {
        ClassInstrumentation instr = Instrumentation.getClassInstrumentation(NumberGenerator.class);
        Method nextNumber = NumberGenerator.class.getDeclaredMethod("nextNumber");
        Field counter = NumberGenerator.class.getDeclaredField("counter");
        Method resetCounter = counter.getType().getDeclaredMethod("set", int.class);
        CodePosition codePosition = instr.beforeCall(nextNumber, resetCounter);
        return instrumentation.createBreakpoint(codePosition, thread);
    }
}
