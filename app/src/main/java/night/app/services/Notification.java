package night.app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import night.app.R;

public class Notification {
    NotificationManager manager;
    NotificationCompat.Builder builder;

    public void post() {
        manager.notify(0, builder.build());
    }

    public void createChannel(String id) {
        NotificationChannel channel =
                new NotificationChannel(id, id, NotificationManager.IMPORTANCE_DEFAULT);

        manager.createNotificationChannel(channel);
    }

    public Notification(Context context, String channelId, String title, String text) {
        manager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));

        // ignore if channel is existed
        createChannel(channelId);

        builder = new NotificationCompat.Builder(context, channelId)
                // small icon can only contains white color
                .setSmallIcon(R.drawable.ic_notification)

                // background color of small icon
                .setColor(Color.parseColor("#7B20FB"))

                // message information
                .setContentTitle(title)
                .setContentText(text);
    }
}
