package night.app.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import night.app.activities.MainActivity;
import night.app.data.entities.Alarm;
import night.app.utils.AlarmSchedule;
import night.app.utils.DatetimeUtils;


public class AlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent nextActivity = new Intent(context, AlarmService.class);
        nextActivity.replaceExtras(intent.getExtras());

        // re-schedule alarm
        new Thread(() -> {
            Alarm alarm = MainActivity.getDatabase().alarmDAO().getById(intent.getExtras().getInt("alarmId"));

            new AlarmSchedule(context)
                    .setRingtone(alarm.ringtoneId)
                    .post(alarm.id, DatetimeUtils.getClosestDateTime(alarm.endTime));
        }).start();

        context.startForegroundService(nextActivity);
    }
}
