package night.app.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import night.app.activities.MainActivity;
import night.app.activities.SleepActivity;
import night.app.data.entities.Sleep;
import night.app.utils.Notification;
import night.app.utils.DatetimeUtils;


public class NotificationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent nextActivity = new Intent(context, SleepActivity.class);
        nextActivity.putExtra("isAlarm", false);

        new Thread(() -> {
            Sleep sleep = MainActivity.getDatabase().sleepDAO().getSleep();
            nextActivity.putExtra("sleepMinutes", (int) (int) TimeUnit.MILLISECONDS.toMinutes(DatetimeUtils.getClosestDateTime(sleep.endTime) - System.currentTimeMillis()));

            PendingIntent pi = PendingIntent.getActivity(context, 4, nextActivity, PendingIntent.FLAG_IMMUTABLE);
            String msg = "Regular Sleeping Schedule Helps Improve Sleep Quality";

            new Notification(context, pi, "Time to Sleep!", msg).post();
        }).start();
    }
}
