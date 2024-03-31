package night.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent nextActivity = new Intent(context, AlarmService.class);
        nextActivity.replaceExtras(intent.getExtras());

        context.startForegroundService(nextActivity);
    }
}
