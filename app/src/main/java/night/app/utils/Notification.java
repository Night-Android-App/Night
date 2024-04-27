package night.app.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import night.app.R;

public class Notification {
    private final android.app.Notification.Builder builder;
    private final NotificationManager manager;

    public android.app.Notification.Builder getBuilder() {
        return builder;
    }

    public void post() {
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public Notification(Context context, PendingIntent pi, String title, String msg) {

        NotificationChannel channel = new NotificationChannel("night", "night", NotificationManager.IMPORTANCE_HIGH);

        manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        builder = new android.app.Notification.Builder(context, "night")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(msg)
                .setContentIntent(pi);
    }
}
