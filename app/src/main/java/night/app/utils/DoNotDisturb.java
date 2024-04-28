package night.app.utils;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class DoNotDisturb {
    private final NotificationManager manager;

    public static void requestPermission(Context context) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (!manager.isNotificationPolicyAccessGranted()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Access DND policy")
                    .setMessage("DND cannot work without this.")
                    .setNegativeButton("Not Now", null)
                    .setPositiveButton("Grant", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        context.startActivity(intent);
                    });

            builder.show();
        }
    }

    public void stop() {
        if (manager.isNotificationPolicyAccessGranted()) {
            manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        }
    }

    public void start() {
        if (manager.isNotificationPolicyAccessGranted()) {
            manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
        }
    }

    public DoNotDisturb(Context context) {
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
