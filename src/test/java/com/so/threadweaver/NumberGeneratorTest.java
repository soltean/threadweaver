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

public class NumberGeneratorTest {

    private String first;
    private String second;
    private NumberGenerator generator;

    @Test
    public void testNextId() throws InterruptedException {

        AnnotatedTestRunner runner = new AnnotatedTestRunner();
        HashSet<String> methods = new HashSet<String>();
        methods.add(NumberGenerator.class.getName() + ".nextNumber");
        runner.setMethodOption(MethodOption.LISTED_METHODS, methods);
        runner.setDebug(false);
        runner.runTests(this.getClass(), NumberGeneratorTest.class);
    }

    @ThreadedBefore
    public void setup() {
        generator = new NumberGenerator();
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
    public void checktResults() {
        Assertions.assertThat(first).isNotEqualTo(second);
    }
}
