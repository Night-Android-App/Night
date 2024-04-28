package night.app.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import night.app.data.entities.Day;
import night.app.data.entities.SleepEvent;

public class DaySample {
    private SleepEvent[] events = new SleepEvent[10];
    private final int[] confidences = new int[] {11, 23,56,72,77,85,38,50,77,72};

    public Day getDay() {
        Day day = new Day();
        day.date = 0L;
        day.startTime = getEvents()[0].timeline;
        day.endTime = getEvents()[9].timeline;

        return day;
    }


    public SleepEvent[] getEvents() {
        events = new SleepEvent[10];
        for (int i=0; i < events.length; i++) {
            events[i] = new SleepEvent(TimeUnit.MINUTES.toMillis(i*10L), confidences[i], 0, 0);
        }

        return events;
    }
}
