package night.app.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import night.app.R;

public class Notification {
    private final android.app.Notification.Builder builder;
    private final NotificationManager manager;

    public static void requestPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Permission required")
                .setMessage("Post notification")
                .setNegativeButton("Not now", null)
                .setPositiveButton("Grant", (dialog, i) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());

                    context.startActivity(intent);
                });

        builder.show();
    }

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
