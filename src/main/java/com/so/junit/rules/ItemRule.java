package com.so.junit.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

public class ItemRule implements TestRule {

    private List<Item> items;

    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                //before
                buildItems();
                base.evaluate();
                //after
                System.out.println("Items created");
            }
        };
    }

    private List<Item> buildItems() {
        items = new ArrayList<>();
        items.add(new Item("code1", 100));
        items.add(new Item("code2", 1500));
        return items;
    }

    public List<Item> getItems() {
        return items;
    }
}
