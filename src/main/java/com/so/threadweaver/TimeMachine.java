package com.so.threadweaver;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class TimeMachine {

    public int getCurrentMinute() {
        return LocalDateTime.now().get(ChronoField.MINUTE_OF_DAY);
    }
}
