package night.app.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.SleepSegmentRequest;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

import night.app.data.entities.Alarm;
import night.app.databinding.ActivitySleepBinding;
import night.app.fragments.dialogs.ConfirmDialog;
import night.app.fragments.dialogs.MissionDialog;
import night.app.services.AlarmSchedule;
import night.app.services.AlarmService;
import night.app.services.RingtonePlayer;
import night.app.services.SleepReceiver;
import night.app.utils.LayoutUtils;
import night.app.utils.TimeUtils;

public class SleepActivity extends AppCompatActivity {
    private ActivitySleepBinding binding;
    private final RingtonePlayer player = new RingtonePlayer(this);

    private Thread countdownThread = null;

    private void wakeup() {
        new MissionDialog().show(getSupportFragmentManager(), null);
    }

    public void disableAlarm() {
        if (AlarmService.getInstance() != null) {
            AlarmService.getInstance().stop();
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) disableAlarm();
    }

    private void countdown(int seconds) {
        countdownThread = new Thread(() -> {
            int remain = seconds;
            while (remain >= 0) {
                try {
                    Thread.sleep(1000);

                    String text = "(" + TimeUtils.toHrMinSec(remain) + ")";
                    runOnUiThread(() -> binding.tvCount.setText(text));
                    remain--;
                } catch (InterruptedException e) {
                }
            }
            runOnUiThread(() -> binding.tvCount.setText("End"));
        });
        countdownThread.start();
    }

    private void updateCurrentTime() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);

                runOnUiThread(() -> {
                    binding.tvCurrent.setText(TimeUtils.toTimeNotation(Calendar.getInstance()));
                });

                updateCurrentTime();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void setWeekOfDay() {
        String[] dayName = new String[]{
                "Sunday", "Monday",
                "Tuesday", "Wednesday",
                "Thursday", "Friday",
                "Saturday"
        };

        int dayNum = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        if (dayNum < dayName.length) {
            binding.tvWeekOfDay.setText(dayName[dayNum]);
        }
    }

    private void gotoAlarmBranch() {
        binding.btnPos.setText("Disable");
        Calendar calendar = Calendar.getInstance();
        binding.tvCurrent.setText(TimeUtils.toTimeNotation(calendar));
        binding.tvWake.setText(TimeUtils.toTimeNotation(calendar));
        updateCurrentTime();

        new Thread(() -> {
            Alarm alarm = MainActivity.getDatabase().alarmDAO().getAlarm(getIntent().getExtras().getInt("alarmId", 0));

            if (alarm != null && alarm.ringtoneId != null) {
                player.setId(alarm.ringtoneId);
            }
            countdown(1);

            MainActivity.getDatabase().alarmDAO().updateAlarmEnabled(alarm.id, 0);
        }).start();
    }

    private void gotoSleepBranch() {
        Integer sleepMinutes = getIntent().getExtras().getInt("sleepMinutes");

        if (sleepMinutes >= 0) {
            Calendar calendar = Calendar.getInstance();
            binding.tvCurrent.setText(TimeUtils.toTimeNotation(calendar));

            calendar.add(Calendar.MINUTE, sleepMinutes);

            new AlarmSchedule(getApplicationContext())
                    .post(-1, calendar.getTimeInMillis());

            binding.tvWake.setText(TimeUtils.toTimeNotation(calendar));

            String text = "(" + TimeUtils.toHrMinSec(sleepMinutes * 60) + ")";
            binding.tvCount.setText(text);
            countdown(sleepMinutes * 60 - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) player.release();
        if (countdownThread != null) countdownThread.interrupt();

        Intent intent = new Intent(getApplicationContext(), SleepReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        ActivityRecognition.getClient(getApplicationContext()).removeSleepSegmentUpdates(pendingIntent);
    }

    @Override
    public void onBackPressed() {
        new ConfirmDialog("Exit page", "You cannot track the sleep if the app end.", (dialog) -> {
            dialog.dismiss();
            finish();
        }).show(getSupportFragmentManager(), null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySleepBinding.inflate(getLayoutInflater());
        binding.setTheme(MainActivity.getAppliedTheme());

        setContentView(binding.getRoot());
        LayoutUtils.setSystemBarColor(getWindow(), Color.parseColor("#1D134A"), Color.parseColor("#221752"));

        updateCurrentTime();
        setWeekOfDay();

        binding.btnPos.setOnClickListener(v -> wakeup());

        if (getIntent().getExtras() == null) return;

        if (getIntent().hasExtra("alarmId")) {
            gotoAlarmBranch();
        } else {
            gotoSleepBranch();
        }

        Intent intent = new Intent(getApplicationContext(), SleepReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("No permission");
            return;
        }

        ActivityRecognition.getClient(getApplicationContext())
                .requestSleepSegmentUpdates(pendingIntent,
                        new SleepSegmentRequest(SleepSegmentRequest.CLASSIFY_EVENTS_ONLY))
                .addOnSuccessListener( o -> {
                    System.out.println("Successfully subscribed to sleep data.");
                })
                .addOnCanceledListener(() -> {
                    System.out.println("Canceled");
                })
                .addOnCompleteListener(o -> {
                    System.out.println("Completed");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Fail to subscribe to sleep data");
                });
    }
}
