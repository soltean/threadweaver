package com.so.threadweaver;

import com.google.testing.threadtester.AnnotatedTestRunner;
import com.google.testing.threadtester.MethodOption;
import com.google.testing.threadtester.ThreadedAfter;
import com.google.testing.threadtester.ThreadedBefore;
import com.google.testing.threadtester.ThreadedMain;
import com.google.testing.threadtester.ThreadedSecondary;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.HashSet;

public class NumberGeneratorSimpleTest {

    private String first;
    private String second;
    private NumberGenerator generator;

    @Test
    public void testNextNumber() throws InterruptedException {

        AnnotatedTestRunner runner = new AnnotatedTestRunner();
        HashSet<String> methods = new HashSet<String>();
        methods.add(NumberGenerator.class.getName() + ".nextNumber");
        runner.setMethodOption(MethodOption.LISTED_METHODS, methods);
        runner.setDebug(true);
        runner.runTests(this.getClass(), NumberGenerator.class);
    }

    @ThreadedBefore
    public void setup() {

        generator = new NumberGenerator(new TimeMachine());
    }

    @ThreadedMain
    public void main() {
        first = generator.nextNumber();
    }

    @ThreadedSecondary
    public void secondary() {
        second = generator.nextNumber();
    }


    @ThreadedAfter
    public void checkResults() {
        Assertions.assertThat(first).isNotEqualTo(second);
    }
}
