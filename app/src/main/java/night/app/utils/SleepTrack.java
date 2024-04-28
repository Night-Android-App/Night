package night.app.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.SleepSegmentRequest;

import night.app.services.SleepReceiver;

public class SleepTrack {
    private final Context context;

    public PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, SleepReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    }

    public void requestPermission() {
        if (context.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {

        }
    }

    public void stop() {
        ActivityRecognition.getClient(context).removeSleepSegmentUpdates(getPendingIntent());
    }

    @SuppressLint("MissingPermission")
    public void start() {
        requestPermission();

        SleepSegmentRequest req = new SleepSegmentRequest(SleepSegmentRequest.CLASSIFY_EVENTS_ONLY);
        ActivityRecognition.getClient(context).requestSleepSegmentUpdates(getPendingIntent(), req);
    }

    public SleepTrack(Context context) {
        this.context = context;
    }
}
