package night.app.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.activities.SleepActivity;
import night.app.data.entities.Sleep;
import night.app.utils.TimeUtils;


public class NotificationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent nextActivity = new Intent(context, SleepActivity.class);
        nextActivity.putExtra("isAlarm", false);

        new Thread(() -> {
            Sleep sleep = MainActivity.getDatabase().sleepDAO().getSleep();
            nextActivity.putExtra("sleepMinutes", (int) (int) TimeUnit.MILLISECONDS.toMinutes(TimeUtils.getClosestDateTime(sleep.endTime) - System.currentTimeMillis()));

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 4, nextActivity, PendingIntent.FLAG_IMMUTABLE);
            NotificationChannel channel = new NotificationChannel("night", "night", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(context, "night")
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Time to sleep!")
                    .setContentText("Regular Sleeping Schedule Helps Improve Sleep Quality")
                    .setContentIntent(pendingIntent);

            manager.notify(0, builder.build());
        }).start();
    }
}
