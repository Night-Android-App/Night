package night.app.utils;

import java.util.ArrayList;
import java.util.List;

import night.app.data.entities.Day;
import night.app.data.entities.SleepEvent;

public class DaySample {
    private SleepEvent[] events = new SleepEvent[10];
    private final int[] confidences = new int[] {11, 23,56,72,77,85,38,50,77,72};

    public SleepEvent[] getEvents() {
        events = new SleepEvent[10];
        for (int i=0; i < events.length; i++) {
            events[i] = new SleepEvent();
            events[i].timeline = (long) i * 10;
            events[i].confidence = confidences[i];
        }

        return events;
    }

    public static List<Day> getDay() {
        List<Day> dayList = new ArrayList<>();

//        S
//        for (int i=0; i < 10; i++) {
//            event
//        }

        Day day = new Day();
        day.date = 0L;
//        day.sleep = "{\"1422\": 0, \"10\": 52, \"60\": 90, \"120\": 86,  \"240\": 54, \"410\": 51,  \"420\": 0}";
        day.dream = "Lorem ipsum is placeholder text commonly used in the graphic, print, and publishing industries for previewing";

        dayList.add(day);
        return dayList;
    }
}
