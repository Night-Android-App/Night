package night.app.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
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
            score += 0.6;
        }
        else {
            long sdLowerLimit = Math.abs(sleepTime - TimeUnit.HOURS.toMillis(7));
            long sdUpperLimit = Math.abs(sleepTime - TimeUnit.HOURS.toMillis(9));

            long minDistance = Math.min(sdLowerLimit, sdUpperLimit);
            score += .6 * Math.max(0,  1 - minDistance / TimeUnit.HOURS.toMillis(2));
        }
        if (getSleepEfficiency() >= 0.85) score += 0.1;
        if (getSleepEfficiency() >= 0.75) score += 0.1;

        if (getFellAsleepDuration() != -1) {
            if (getFellAsleepDuration() <= 2760) score += 0.05;
            if (getFellAsleepDuration() <= 1800) score += 0.05;
        }

        int awake = -2;
        for (SleepEvent event : events) {
            if (event.confidence < 50) awake++;
        }

        if (awake <= 3) score += 0.05;
        if (awake <= 1) score += 0.05;

        return (int) Math.round(score*100);
    }

    public double getSleepEfficiency() {
        return (double) getConfidenceDuration(50, 100) / (double) (getInBedDuration() == 0 ? 1 : getInBedDuration());
    }

    public long getInBedDuration() {
        if (events.length > 1) {
            return events[events.length-1].timeline - events[0].timeline;
        }
        return 0;
    }

    public long getFellAsleepDuration() {
        long durationInMills = 0;

        for (int i=0; i <events.length; i++) {
            if (events[i].confidence >= 50) return durationInMills;
            durationInMills += getEventDuration(i);
        }
        return -1;
    }

    public String[] getTimelines() {
        String[] time = new String[events.length];

        for (int i=0; i < events.length; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(events[i].timeline);

            time[i] = calendar.get(Calendar.HOUR_OF_DAY) + ":" +calendar.get(Calendar.MINUTE);
        }

        return time;
    }

    public Integer[] getConfidences() {
        Integer[] confidences = new Integer[events.length];

        for (int i=0; i < events.length; i++) {
            confidences[i] = events[i].confidence;
        }

        return confidences;
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
