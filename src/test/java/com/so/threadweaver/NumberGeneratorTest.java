package com.so.threadweaver;

import com.google.testing.threadtester.ClassInstrumentation;
import com.google.testing.threadtester.CodePosition;
import com.google.testing.threadtester.Instrumentation;
import com.google.testing.threadtester.InterleavedRunner;
import com.google.testing.threadtester.MainRunnableImpl;
import com.google.testing.threadtester.RunResult;
import com.google.testing.threadtester.SecondaryRunnableImpl;
import com.google.testing.threadtester.ThreadedTest;
import com.google.testing.threadtester.ThreadedTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class NumberGeneratorTest {

    private String first;
    private String second;
    private NumberGenerator generator;

    ThreadedTestRunner runner = new ThreadedTestRunner();

    @Test
    public void testThreadedTests() {
        runner.runTests(getClass(), NumberGenerator.class);
    }

    @ThreadedTest
    public void testConcurrencyIssue() throws NoSuchMethodException, NoSuchFieldException {
        TimeMachine timeMachine = MockedTimeMachine.getTimeMachineMock();
        generator = new NumberGenerator(timeMachine);
        RunResult result = InterleavedRunner.interleave(new FirstThread(), new SecondThread(), Arrays.asList(getCodePosition()));
        result.throwExceptionsIfAny();
    }

    private class FirstThread extends MainRunnableImpl<NumberGenerator> {

        @Override
        public Class<NumberGenerator> getClassUnderTest() {
            return NumberGenerator.class;
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
            first = generator.nextNumber();
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
            second = localNumberGenerator.nextNumber();
        }
    }

    private CodePosition getCodePosition() throws NoSuchMethodException, NoSuchFieldException {
        ClassInstrumentation instr = Instrumentation.getClassInstrumentation(NumberGenerator.class);
        Method nextNumber = NumberGenerator.class.getDeclaredMethod("nextNumber");
        Field counter = NumberGenerator.class.getDeclaredField("counter");
        Method resetCounter = counter.getType().getDeclaredMethod("set", int.class);
        return instr.beforeCall(nextNumber, resetCounter);
    }


   /* private CodePosition getCodePosition() {
        MethodRecorder<NumberGenerator> recorder = new MethodRecorder<>(NumberGenerator.class);
        NumberGenerator control = recorder.getControl();
        AtomicInteger target = recorder.createTarget(AtomicInteger.class);
        return recorder.in(control.nextNumber()).afterCalling(target.set(0)).position();
    }*/

/*    private CodePosition getCodePositionV3() throws NoSuchMethodException {
        ClassInstrumentation instr = Instrumentation.getClassInstrumentation(NumberGenerator.class);
        return instr.afterCall("nextNumber", "set");
    }*/
}
