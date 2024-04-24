package night.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.SleepClassifyEvent;

import java.util.List;

import night.app.activities.MainActivity;

public class SleepReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("SleepReceiver onReceive");

        // if captured a sleep classify event
        if (SleepClassifyEvent.hasEvents(intent)) {
            List<SleepClassifyEvent> sleepClassifyEvents = SleepClassifyEvent.extractEvents(intent);

            for (SleepClassifyEvent event : sleepClassifyEvents) {
                // If user closed the app, then database will be null
                if (MainActivity.getDatabase() != null) {
                    new Thread(() -> {
                        // store event to database
                        MainActivity.getDatabase().sleepEventDAO().create(
                                event.getTimestampMillis(),
                                event.getConfidence(),
                                event.getLight(),
                                event.getMotion()
                        );
                    }).start();
                }
            }
        }
    }
}