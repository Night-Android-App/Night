package night.app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import night.app.R;
import night.app.activities.SleepActivity;
import night.app.utils.Notification;
import night.app.utils.RingtonePlayer;

public class AlarmService extends Service {
    Intent intent;
    private static AlarmService instance;

    public static AlarmService getInstance() {
        return instance;
    }

    public void stop() {
        stopSelf();
        instance = null;
    }

    private RingtonePlayer player = null;

    private void playRingtone() {

        player = new RingtonePlayer(getApplicationContext());
        if (intent != null && intent.hasExtra("ringtoneId")) {
            player.setId(intent.getExtras().getInt("ringtoneId"));
        }
        player.run();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        this.intent = intent;
        instance = this;

        Intent nextActivity = new Intent(getApplicationContext(), SleepActivity.class);
        nextActivity.putExtra("isAlarm", true);

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, nextActivity, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new Notification(getApplicationContext(), pi, "Night", "Wake up!");
        startForeground(1, notification.getBuilder().build());

        playRingtone();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }
}