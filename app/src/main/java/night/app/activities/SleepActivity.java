package night.app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.SleepSegmentRequest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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

    private void countdown() {
        if (getIntent().hasExtra("sleepMinutes")) {
            int sleepMinutes = getIntent().getExtras().getInt("sleepMinutes");

            Calendar calendar = Calendar.getInstance();
            binding.tvCurrent.setText(TimeUtils.toTimeNotation(calendar));

            calendar.add(Calendar.MINUTE, sleepMinutes);

            new AlarmSchedule(getApplicationContext())
                    .post(-1, calendar.getTimeInMillis());

            binding.tvWake.setText(TimeUtils.toTimeNotation(calendar));

            countdownThread = new Thread(() -> {
                int remain = sleepMinutes * 60 - 1;
                while (remain >= 0) {
                    try {
                        Thread.sleep(1000);

                        String text = "(" + TimeUtils.toHrMinSec(remain) + ")";
                        runOnUiThread(() -> binding.tvCount.setText(text));
                        remain--;
                    }
                    catch (InterruptedException e) { }
                }
                runOnUiThread(() -> binding.tvCount.setText("End"));
            });
            countdownThread.start();
        }
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

    private void gotoSleepBranch() {
        int sleepMinutes = getIntent().getExtras().getInt("sleepMinutes");

        if (sleepMinutes >= 0) countdown();

        Intent intent = new Intent(getApplicationContext(), SleepReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("No permission");
            return;
        }

        new Thread(() -> {
            long startTime = (int) (System.currentTimeMillis() - TimeUtils.getTodayAtMidNight());
            long endTime = startTime + (int) TimeUnit.MINUTES.toMillis(getIntent().getExtras().getInt("sleepMinutes", 0));

            MainActivity.getDatabase().dayDAO().create(
                    TimeUtils.getTodayAtMidNight(), startTime, endTime, null
            );
        }).start();

        ActivityRecognition.getClient(getApplicationContext())
                .requestSleepSegmentUpdates(pendingIntent,
                        new SleepSegmentRequest(SleepSegmentRequest.CLASSIFY_EVENTS_ONLY))
                .addOnSuccessListener( o -> {
                    System.out.println("Successfully subscribed to sleep data.");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Fail to subscribe to sleep data");
                });
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Exit page")
                .setMessage("You cannot track the sleep if the app end.")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("OK", (dialog, which) -> {
                    finish();
                });

        builder.show();
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


        if (getIntent().hasExtra("isAlarm")) {
            if (getIntent().getExtras().getBoolean("isAlarm")) {
                binding.tvCurrent.setText(TimeUtils.toTimeNotation(Calendar.getInstance()));
                return;
            }
            gotoSleepBranch();
        }
        else {
            countdown();
        }
    }
}
