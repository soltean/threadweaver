package com.so.threadweaver;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

public class MockedTimeMachine {

    public static TimeMachine getTimeMachineMock() {
        TimeMachine timeMachine = createMock(TimeMachine.class);
        //the first thread enters
        //      int min = timeMachine.getCurrentMinute();
        // and gets the minute = 1
        expect(timeMachine.getCurrentMinute()).andReturn(1);
        //the first thread checks if the minute has changed
        //      if (timeMachine.getCurrentMinute() == min) {
        //see it has changed = 2
        expect(timeMachine.getCurrentMinute()).andReturn(2);

        //the second thread enters
        //      int min = timeMachine.getCurrentMinute();
        // and gets the minute = 1
        expect(timeMachine.getCurrentMinute()).andReturn(1);
        //the second thread checks if the minute has changed
        //      if (timeMachine.getCurrentMinute() == min) {
        //see it has changed = 2
        expect(timeMachine.getCurrentMinute()).andReturn(2);

        // second thread enters timeMachine.getCurrentMinute() + "" + counter.incrementAndGet();
        expect(timeMachine.getCurrentMinute()).andReturn(2);
        // first thread enters timeMachine.getCurrentMinute() + "" + counter.incrementAndGet();
        expect(timeMachine.getCurrentMinute()).andReturn(2);

        replay(timeMachine);
        return timeMachine;
    }
}
