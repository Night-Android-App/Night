package night.app.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmSchedule {
    private final Context context;
    private final Intent intent;

    public AlarmSchedule setRingtone(Integer id) {
        intent.putExtra("ringtoneId", id);
        return this;
    }

    public void post(int alarmId, long timestamp) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        intent.putExtra("alarmId", alarmId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, alarmId, intent, PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
    }

    public void cancel(int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        intent.putExtra("alarmId", alarmId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, alarmId, intent, PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent);
    }

    public AlarmSchedule(Context context) {
        this.context = context;
        this.intent = new Intent(context, AlarmReceiver.class);
    }
}
