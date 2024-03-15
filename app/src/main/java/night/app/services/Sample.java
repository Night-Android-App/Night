package night.app.services;

import java.util.ArrayList;
import java.util.List;

import night.app.data.Day;

public class Sample {
    public static List<Day> getDay() {
        List<Day> dayList = new ArrayList<>();

        Day day = new Day();
        day.date = 0;
        day.sleep = "{\"1422\": 0, \"10\": 52, \"60\": 90, \"120\": 86,  \"240\": 54, \"410\": 51,  \"420\": 0}";
        day.dream = "Lorem ipsum is placeholder text commonly used in the graphic, print, and publishing industries for previewing";

        dayList.add(day);
        return dayList;
    }
}
