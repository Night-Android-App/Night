package night.app.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import night.app.activities.MainActivity;
import night.app.data.entities.Day;
import night.app.data.entities.SleepEvent;
import night.app.utils.TimeUtils;

public class SleepData {
    private SleepEvent[] events;

    private boolean isInRange(long data, long start, long end) {
        return data >= start && data <= end;
    }

    public int getScore() {
        double score = 0;

        long sleepTime = getConfidenceDuration(50, 100);
        if (sleepTime <= 0) return 0;


        if (isInRange(sleepTime, TimeUnit.HOURS.toMillis(7), TimeUnit.HOURS.toMillis(9))) {
            score += 0.9;
        }
        else {
            long sdLowerLimit = Math.abs(sleepTime - TimeUnit.HOURS.toMillis(7));
            long sdUpperLimit = Math.abs(sleepTime - TimeUnit.HOURS.toMillis(9));

            score += .9 * (1- (double) Math.min(sdLowerLimit, sdUpperLimit)/120);
        }

        if (getSleepEfficiency() >= 0.85) score += 0.1;
        if (getSleepEfficiency() >= 0.75) score += 0.1;

        if (getFellAsleepDuration() != -1) {
            if (getFellAsleepDuration() <= 2760) score += 0.1;
            if (getFellAsleepDuration() <= 1800) score += 0.1;
        }

        int awake = -2;
        for (SleepEvent event : events) {
            if (event.confidence < 50) awake++;
        }

        if (awake <= 3) score += 0.1;
        if (awake <= 1) score += 0.1;

        return (int) Math.round(score*100);
    }

    public double getSleepEfficiency() {
        return (double) (getConfidenceDuration(50, 100) / getInBedDuration());
    }

    public long getInBedDuration() {
        return events[events.length-1].timeline - events[0].timeline;
    }

    public static SleepData[] dayListToSleepDataArray(List<Day> dayList) {
        SleepData[] sleepData = new SleepData[dayList.size()];
        for (int i=0; i < dayList.size(); i++) {
//            sleepData[i] = new SleepData(dayList.get(i).sleep);
        }

        return sleepData;
    }

    public long getFellAsleepDuration() {
        long durationInMills = 0;

        for (int i=0; i <events.length; i++) {
            if (events[i].confidence >= 50) return durationInMills;
            durationInMills += getEventDuration(i);
        }
        return -1;
    }

    public long getEventDuration(int index) {
        // the duration of last event is 0
        if (index+1 >= events.length) return 0;

        // return in Mills
        return events[index+1].timeline - events[index].timeline;
    }

    public long getConfidenceDuration(int min, int max) {
        long durationInMills = 0;

        for (int i=0; i <events.length; i++) {
            if (events[i].confidence >= min && events[i].confidence <= max) {
                durationInMills += getEventDuration(i);
            }
        }

        return durationInMills;
    }

    public SleepData(SleepEvent[] e) {
        events = e;
    }
}
