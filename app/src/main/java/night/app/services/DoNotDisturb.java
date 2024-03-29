package night.app.services;

import android.app.NotificationManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class DoNotDisturb {
    private final NotificationManager notificationManager;

    public void enable() {
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
    }

    public void disable() {
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
    }

    public DoNotDisturb(AppCompatActivity activity) {
        notificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
