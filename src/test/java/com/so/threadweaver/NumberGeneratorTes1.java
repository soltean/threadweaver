package com.so.threadweaver;

import com.google.testing.threadtester.ClassInstrumentation;
import com.google.testing.threadtester.CodePosition;
import com.google.testing.threadtester.Instrumentation;
import com.google.testing.threadtester.InterleavedRunner;
import com.google.testing.threadtester.MainRunnableImpl;
import com.google.testing.threadtester.MethodRecorder;
import com.google.testing.threadtester.RunResult;
import com.google.testing.threadtester.SecondaryRunnableImpl;
import com.google.testing.threadtester.ThreadedTest;
import com.google.testing.threadtester.ThreadedTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberGeneratorTes1 {

    private String first;
    private String second;
    private NumberGenerator generator;

    ThreadedTestRunner runner = new ThreadedTestRunner();

    @Test
    public void testThreadedTests() {
        runner.runTests(getClass(), NumberGenerator.class);
    }


    @ThreadedTest
    public void setup() throws NoSuchMethodException {
        generator = new NumberGenerator();
        MethodRecorder<NumberGenerator> recorder = new MethodRecorder<>(generator);
        RunResult result = InterleavedRunner.interleave(new FirstThread(), new SecondThread(), Arrays.asList(getCodePositionV2()));
        result.throwExceptionsIfAny();
    }

    private class FirstThread extends MainRunnableImpl<NumberGenerator> {

        @Override
        public Class<NumberGenerator> getClassUnderTest() {
            return NumberGenerator.class;
        }

        @Override
        public void initialize() throws Exception {
            generator = new NumberGenerator();
        }

        @Override
        public NumberGenerator getMainObject() {
            return generator;
        }

        @Override
        public void terminate() throws Exception {
            Assertions.assertThat(first).isNotEqualTo(second);
        }

        @Override
        public void run() throws Exception {
            System.out.println("Running main thread");
            first = generator.nextNumber();
            System.out.printf("First thread finished\n");
        }
    }

    private class SecondThread extends SecondaryRunnableImpl<NumberGenerator, FirstThread> {

        private NumberGenerator localNumberGenerator;

        @Override
        public void initialize(FirstThread main) throws Exception {
            localNumberGenerator = main.getMainObject();
        }

        @Override
        public void run() throws Exception {
            System.out.printf("Second thread started\n");
            second = localNumberGenerator.nextNumber();
            System.out.printf("Second thread finished\n");
        }
    }

   /* private CodePosition getCodePosition() {
        MethodRecorder<NumberGenerator> recorder = new MethodRecorder<>(NumberGenerator.class);
        NumberGenerator control = recorder.getControl();
        AtomicInteger target = recorder.createTarget(AtomicInteger.class);
        return recorder.in(control.nextNumber()).afterCalling(target.set(0)).position();
    }*/

    private CodePosition getCodePositionV2() throws NoSuchMethodException {
        ClassInstrumentation instr = Instrumentation.getClassInstrumentation(NumberGenerator.class);
        Method nextNumber = NumberGenerator.class.getDeclaredMethod("nextNumber");
        Method resetCounter = AtomicInteger.class.getDeclaredMethod("set", int.class);
        return instr.afterCall(nextNumber, resetCounter);
    }

    private CodePosition getCodePositionV3() throws NoSuchMethodException {
        ClassInstrumentation instr = Instrumentation.getClassInstrumentation(NumberGenerator.class);
        return instr.afterCall("nextNumber", "set");
    }
}
