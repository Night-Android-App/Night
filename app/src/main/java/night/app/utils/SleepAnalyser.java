package night.app.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import night.app.data.entities.SleepEvent;

public class SleepAnalyser {
    private final SleepEvent[] events;

    public int getScore() {
        double score = 0;

        long sleepTime = getConfidenceDuration(50, 100);
        if (sleepTime <= 0) return 0;

        if (sleepTime >= TimeUnit.HOURS.toMillis(7) && sleepTime <= TimeUnit.HOURS.toMillis(9)) {
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
        double sleepInMills = getConfidenceDuration(50, 100);
        double inBedInMills = Math.min(1, getInBedDuration()); // avoid division of 0

        return sleepInMills / inBedInMills;
    }

    public long getInBedDuration() {
        if (events.length == 0) return 0;

        // difference between first event and last event (in millisecond)
        return events[events.length-1].timeline - events[0].timeline;
    }

    public long getFellAsleepDuration() {
        long durationInMills = 0;

        for (int i=0; i <events.length; i++) {
            // find the first event that enters sleep stage
            if (events[i].confidence >= 50) return durationInMills;

            durationInMills += getEventDuration(i);
        }

        // user did not enter the sleep stage
        return -1;
    }

    public String[] getTimelines() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        return Arrays.stream(events).map(e -> format.format(e.timeline)).toArray(String[]::new);
    }

    public int[] getConfidences() {
        return Arrays.stream(events).mapToInt(event -> event.confidence).toArray();
    }

    public long getEventDuration(int index) {
        // the duration of last event is 0
        if (index+1 >= events.length) return 0;

        // return in Mills
        return events[index+1].timeline - events[index].timeline;
    }

    public long getConfidenceDuration(int min, int max) {
        long durationInMills = 0;

        // get a total duration of event within a specific range
        for (int i=0; i <events.length; i++) {
            if (events[i].confidence >= min && events[i].confidence <= max) {
                durationInMills += getEventDuration(i);
            }
        }

        return durationInMills;
    }

    public SleepAnalyser(SleepEvent[] e) {
        events = e;
    }
}
