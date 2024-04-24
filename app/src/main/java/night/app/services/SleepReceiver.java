package night.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.SleepClassifyEvent;
import com.google.android.gms.location.SleepSegmentEvent;

import java.util.List;

import night.app.activities.MainActivity;

public class SleepReceiver extends BroadcastReceiver {
    private static final String TAG = "SleepDataReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("SleepReceiver onReceive");

        if (SleepClassifyEvent.hasEvents(intent)) {
            List<SleepClassifyEvent> sleepClassifyEvents = SleepClassifyEvent.extractEvents(intent);
            for (SleepClassifyEvent event : sleepClassifyEvents) {
                if (MainActivity.getDatabase() != null) {
                    new Thread(() -> {
                        MainActivity.getDatabase().sleepEventDAO().createEvent(
                                event.getTimestampMillis() / 1000,
                                event.getConfidence(),
                                event.getLight(),
                                event.getMotion()
                        );
                    }).start();
                    System.out.println(event.getTimestampMillis() + " " + event.getConfidence() + " " + event.getMotion());
                }
            }
        }
    }
}