package night.app.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import night.app.R;
import night.app.activities.AgreementActivity;
import night.app.activities.SleepActivity;

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

        playRingtone();

        instance = this;

        Intent nextActivity = new Intent(getApplicationContext(), SleepActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, nextActivity, PendingIntent.FLAG_IMMUTABLE);

        NotificationChannel channel =
                new NotificationChannel("night", "night", NotificationManager.IMPORTANCE_LOW);

        NotificationManager manager = getSystemService(NotificationManager.class);

        manager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "night")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Night")
                .setContentText("Wake up!")
                .setContentIntent(pendingIntent);

        startForeground(1, builder.build());

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