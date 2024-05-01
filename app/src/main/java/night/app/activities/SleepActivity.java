package night.app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.SleepSegmentRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import night.app.data.entities.Alarm;
import night.app.data.entities.Day;
import night.app.data.entities.Sleep;
import night.app.databinding.ActivitySleepBinding;
import night.app.fragments.dialogs.MissionDialog;
import night.app.utils.AlarmSchedule;
import night.app.services.AlarmService;
import night.app.utils.DoNotDisturb;
import night.app.utils.RingtonePlayer;
import night.app.services.SleepReceiver;
import night.app.utils.LayoutUtils;
import night.app.utils.DatetimeUtils;
import night.app.utils.SleepTrack;

public class SleepActivity extends AppCompatActivity {
    private ActivitySleepBinding binding;
    private final RingtonePlayer player = new RingtonePlayer(this);

    private SleepTrack sleepTrack;
    private DoNotDisturb dnd;

    private Thread countdownThread = null;
    private final long date = DatetimeUtils.getTodayAtMidNight();

    private void wakeup() {
        new Thread(() -> {
            if (sleepTrack != null) {
                if (MainActivity.getDatabase().sleepDAO().getSleep().enableMission == 1) {
                    new MissionDialog().show(getSupportFragmentManager(), null);
                }
                else {
                    finish();
                }
            }
            else if (getIntent().hasExtra("isAlarm")){
                Alarm alarm = MainActivity.getDatabase().alarmDAO().getById(getIntent().getExtras().getInt("alarmId"));
                if (alarm != null && alarm.enableMission == 1) {
                    new MissionDialog().show(getSupportFragmentManager(), null);
                }
                else {
                    AlarmService.getInstance().stopSelf();
                    finish();
                }
            }
            else {
                new MissionDialog().show(getSupportFragmentManager(), null);
            }
        }).start();
    }

    public void disableAlarm() {
        if (AlarmService.getInstance() != null) {
            AlarmService.getInstance().stop();
        }
    }

    public void showDreamRecord() {
        @SuppressLint("RestrictedApi")
        AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Record dream? (if any)")
                .setNegativeButton("No", (dialog, which) -> finish())
                .setPositiveButton("Yes", (dialog, which) -> {
                    EditText et = new EditText(this);

                    new AlertDialog.Builder(this)
                            .setView(et, 24, 24, 24, 24)
                            .setTitle("Enter dream record")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Done", (d, i) -> {
                                System.out.println("?");

                                String dreamRecord = et.getText().toString();
                                new Thread(() -> {
                                    MainActivity.getDatabase().dayDAO().updateDream(date, dreamRecord);
                                    finish();
                                }).start();
                            })
                            .setCancelable(false)
                            .show();
                });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            disableAlarm();

            if (sleepTrack == null) finish();
            else showDreamRecord();
        }
    }

    private void countdown(int sleepMinutes) {
        Calendar calendar = Calendar.getInstance();
        binding.tvCurrent.setText(DatetimeUtils.toTimeNotation(calendar));

        calendar.add(Calendar.MINUTE, sleepMinutes);
        binding.tvWake.setText(DatetimeUtils.toTimeNotation(calendar));

        countdownThread = new Thread(() -> {
            int remain = sleepMinutes * 60 - 1;
            while (remain >= 0) {
                try {
                    Thread.sleep(1000);

                    String text = "(" + DatetimeUtils.toHrMinSec(remain) + ")";
                    runOnUiThread(() -> binding.tvCount.setText(text));
                    remain--;
                }
                catch (InterruptedException e) { }
            }
            runOnUiThread(() -> binding.tvCount.setText("End"));

            if (dnd != null) dnd.stop();

            if (sleepTrack != null) {
                Integer prodId = MainActivity.getDatabase().sleepDAO().getSleep().ringtoneId;
                if (prodId != null) player.setId(prodId);
            }

            player.run();
        });
        countdownThread.start();
    }

    private void updateCurrentTime() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);

                runOnUiThread(() -> {
                    binding.tvCurrent.setText(DatetimeUtils.toTimeNotation(Calendar.getInstance()));
                });

                updateCurrentTime();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void setWeekOfDay() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        binding.tvWeekOfDay.setText(format.format(System.currentTimeMillis()));
    }

    private void gotoSleepBranch() {
        new Thread(() -> {
            Sleep sleep = MainActivity.getDatabase().sleepDAO().getSleep();
            long startTime = System.currentTimeMillis() - DatetimeUtils.getTodayAtMidNight();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(DatetimeUtils.getClosestDateTime(sleep.endTime));

            if (sleep.enableDND == 1) {
                dnd = new DoNotDisturb(this);
                dnd.start();
            }

            long endTime = DatetimeUtils.getClosestDateTime(sleep.endTime) - DatetimeUtils.getTodayAtMidNight();

            runOnUiThread(() -> countdown((int) TimeUnit.MILLISECONDS.toMinutes(endTime - startTime)));
            MainActivity.getDatabase().dayDAO().create(DatetimeUtils.getTodayAtMidNight(), startTime, endTime);
        }).start();

        sleepTrack = new SleepTrack(this);
        sleepTrack.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) player.release();
        if (countdownThread != null) countdownThread.interrupt();

        if (sleepTrack != null) {
            new Thread(() -> {
                MainActivity.getDatabase().dayDAO().updateEndTime(date, System.currentTimeMillis() - date);
            }).start();

            sleepTrack.stop();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Exit page")
                .setMessage("You cannot track the sleep if the app end.")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("OK", (dialog, which) -> finish());

        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySleepBinding.inflate(getLayoutInflater());
        binding.setTheme(MainActivity.getAppliedTheme());

        setContentView(binding.getRoot());
        LayoutUtils.setSystemBarColor(getWindow(), 0xFF1D134A, 0xFF221752);

        updateCurrentTime();
        setWeekOfDay();

        binding.btnPos.setOnClickListener(v -> wakeup());

        if (getIntent().hasExtra("isAlarm")) {
            if (getIntent().getExtras().getBoolean("isAlarm")) {
                binding.tvCurrent.setText(DatetimeUtils.toTimeNotation(Calendar.getInstance()));
                return;
            }
            new Thread(() -> {
                Day day = MainActivity.getDatabase().dayDAO().getByDate(DatetimeUtils.getTodayAtMidNight());
                runOnUiThread(() -> {
                    if (day != null) {
                        new AlertDialog.Builder(this)
                                .setTitle("You already have record today.")
                                .setMessage("Do you want to replace it?")
                                .setNegativeButton("No", (dialog, which) -> finish())
                                .setPositiveButton("Yes", (dialog, which) -> gotoSleepBranch())
                                .setCancelable(false)
                                .show();
                    }
                    else {
                        gotoSleepBranch();
                    }
                });
            }).start();
        }
        else {
            if (getIntent().hasExtra("sleepMinutes")) {
                int sleepMinutes = getIntent().getExtras().getInt("sleepMinutes");
                countdown(sleepMinutes);
            }
        }
    }
}
