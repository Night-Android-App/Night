package night.app.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import night.app.data.Day;
import night.app.utils.TimeUtils;

public class SleepData {
    private final Integer[] timelines;
    private final Integer[] confidences;

    public Integer[] getTimelines() { return timelines; }
    public Integer[] getConfidences() { return confidences; }

    private boolean isInRange(int data, int start, int end) {
        return data >= start && data <= end;
    }

    public int getScore() {
        double score = 0;

        int sleepTime = getTotalSecondsByConfidence(50, 101);
        if (sleepTime <= 0) return 0;

        if (isInRange(sleepTime, 420, 540)) score += 0.3;
        if (isInRange(sleepTime, 300, 420)) score += 0.3;
        else if (isInRange(sleepTime, 540, 660)) score += 0.3;

        if (getSleepEfficiency() >= 0.85) score += 0.1;
        if (getSleepEfficiency() >= 0.75) score += 0.1;

        if (getFellAsleepTime() != -1) {
            if (getFellAsleepTime() <= 2760) score += 0.1;
            if (getFellAsleepTime() <= 1800) score += 0.1;
        }

        int awake = -2;
        for (Integer confidence : confidences) {
            if (confidence < 50) awake++;
        }

        if (awake <= 3) score += 0.1;
        if (awake <= 1) score += 0.1;

        return (int) Math.round(score*100);
    }

    public double getSleepEfficiency() {
        if (timelines.length == 0) return -1;

        int inBedSeconds = TimeUtils.timeDiff(timelines[0], timelines[timelines.length-1]);
        int sleepSeconds = getTotalSecondsByConfidence(50, 101);
        return (double) sleepSeconds / inBedSeconds;
    }

    public Integer getTotalSleep() {
        if (timelines.length == 0) return 0;

        int prevTime = timelines[0];
        int laterTime = timelines[timelines.length-1];

        if (prevTime > laterTime) {
            return 24 * 60 + laterTime - prevTime;
        }
        return laterTime - prevTime;
    }

    public static SleepData[] dayListToSleepDataArray(List<Day> dayList) {
        SleepData[] sleepData = new SleepData[dayList.size()];
        for (int i=0; i < dayList.size(); i++) {
            sleepData[i] = new SleepData(dayList.get(i).sleep);
        }

        return sleepData;
    }

    public int getFellAsleepTime() {
        if (confidences.length == 0) return -1;

        for (int i=0; i < confidences.length; i++) {
            if (confidences[i] >= 50) {
                return TimeUtils.timeDiff(timelines[0], timelines[i]);
            }
        }
        return TimeUtils.timeDiff(timelines[0], timelines[timelines.length-1]);
    }

    public int getEventDuration(int index) {
        if (index+1 >= timelines.length) return 0;

        return TimeUtils.timeDiff(timelines[index], timelines[index+1]);
    }

    public int getTotalSecondsByConfidence(int min, int max) {
        int seconds = 0;

        for (int i=0; i < confidences.length; i++) {
            if (confidences[i] >= min && confidences[i] < max) seconds += getEventDuration(i);
        }
        return seconds;
    }

    public SleepData(String str) {
        JSONObject json;

        try { json = new JSONObject(str); }
        catch (JSONException e) {
            timelines = new Integer[] {};
            confidences = new Integer[] {};
            return;
        }

        List<Integer> timelines = new ArrayList<>();
        List<Integer> confidence = new ArrayList<>();

        for (Iterator<String> it = json.keys(); it.hasNext();) {
            String key = it.next();
            timelines.add(Integer.parseInt(key));

            confidence.add(json.optInt(key));
        }

        this.timelines = timelines.toArray(new Integer[0]);
        this.confidences = confidence.toArray(new Integer[0]);
    }
}
