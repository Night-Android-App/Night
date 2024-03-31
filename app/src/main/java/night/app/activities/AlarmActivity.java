package night.app.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.Calendar;
import java.util.List;

import night.app.data.Alarm;
import night.app.data.Ringtone;
import night.app.data.Sleep;
import night.app.data.dao.AlarmDAO;
import night.app.databinding.ActivityAlarmBinding;
import night.app.fragments.dialogs.ConfirmDialog;
import night.app.services.AlarmReceiver;
import night.app.utils.LayoutUtils;
import night.app.utils.TimeUtils;

public class AlarmActivity extends AppCompatActivity {
    public final static int TYPE_SLEEP = 0;
    public final static int TYPE_ALARM = 1;

    private int type = TYPE_ALARM;


    private final static int NP_SLEEP = 0;
    private final static int NP_WAKE = 1;

    private int npTarget = NP_WAKE;

    private int sleepTime;
    private int wakeUpTime;
    private Integer prodId = null;

    private boolean isUpdate = false;


    private ActivityAlarmBinding binding;

    private void discardAlarm() {
        new ConfirmDialog("Discard changed settings", "Are you sure?", (dialog) -> {
            dialog.dismiss();
            finish();
        }).show(getSupportFragmentManager(), null);
    }

    private void updateExistedAlarm() {
        AlarmDAO alarmDAO = MainActivity.getDatabase().alarmDAO();
        int enableMission = binding.swMission.isChecked() ? 1 : 0;

        new Thread(() -> {
            alarmDAO.updateAlarm(wakeUpTime, enableMission, prodId);
            finish();
        }).start();
    }

    private void scheduleAlarm() {
        AlarmDAO alarmDAO = MainActivity.getDatabase().alarmDAO();


        int delayInSeconds;
        if (TimeUtils.getTodayHrMin() > wakeUpTime) {
            delayInSeconds = (wakeUpTime + (23*60 - TimeUtils.getTodayHrMin()))*60 - Calendar.getInstance().get(Calendar.MINUTE) * 60;
        }
        else {
            delayInSeconds = (wakeUpTime - TimeUtils.getTodayHrMin())*60- Calendar.getInstance().get(Calendar.MINUTE)*60;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("ringtoneId", alarmDAO.getLastAlarm().ringtoneId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // alarmManager will trigger at least 5 seconds after
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayInSeconds * 1000L, pendingIntent);
    }

    private void saveAlarm() {
        if (type == TYPE_SLEEP) {
            binding.swDnd.isChecked();

        }
        else if (type == TYPE_ALARM) {
            AlarmDAO alarmDAO = MainActivity.getDatabase().alarmDAO();

            if (isUpdate) {
                updateExistedAlarm();
                return;
            }
            new Thread(() -> {
                int enableMission = binding.swMission.isChecked() ? 1 : 0;

                alarmDAO.createAlarm(wakeUpTime,  enableMission, prodId);

                scheduleAlarm();
                finish();
            }).start();
        }
    }

    private void handlePickerValueChanged(NumberPicker np, int old, int newValue) {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.HOUR, binding.npHrs.getValue());
        calendar.add(Calendar.MINUTE, binding.npMins.getValue());

        int hrValue = binding.npHrs.getValue() * 60;
        int minValue = binding.npMins.getValue();

        if (npTarget == NP_WAKE) {
            wakeUpTime = hrValue + minValue;
            binding.tvWakeUp.setText(TimeUtils.toTimeNotation(wakeUpTime*60));
        }
        else if (npTarget == NP_SLEEP) {
            sleepTime = hrValue + minValue;
            binding.tvSleep.setText(TimeUtils.toTimeNotation(sleepTime*60));
        }
    }

    private void updateNumberPickers(int minutes) {
        binding.npHrs.setValue(minutes / 60);
        binding.npMins.setValue(minutes - binding.npHrs.getValue() * 60);
    }

    private void initSleepSettingStyles() {
        binding.llToggleTime.setVisibility(View.VISIBLE);
        binding.swDnd.setVisibility(View.VISIBLE);

        type = TYPE_SLEEP;
        npTarget = NP_SLEEP;

        binding.tvTitle.setText("Sleep Settings");
    }

    private void loadAlarmSettings(int alarmId) {
        new Thread(() -> {
            Alarm alarm = MainActivity.getDatabase().alarmDAO().getAlarm(alarmId);
            prodId = alarm.ringtoneId;

            if (alarm.ringtoneId != null) {
                List<Ringtone> ringtone = MainActivity.getDatabase().dao().getRingtone(alarm.ringtoneId);

                if (ringtone.size() == 0) return;
                runOnUiThread(() -> binding.tvRingName.setText(ringtone.get(0).name));
            }

            runOnUiThread(() -> {
                binding.swMission.setChecked(alarm.enableMission == 1);

                wakeUpTime = alarm.endTime;
                updateNumberPickers(wakeUpTime);
            });
        }).start();
    }

    private void gotoSleepBranch() {
        initSleepSettingStyles();

        binding.llSleep.setOnClickListener(v -> {
            binding.setNpType(NP_SLEEP);
            updateNumberPickers(sleepTime);
        });

        binding.llWakeUp.setOnClickListener(v -> {
            binding.setNpType(NP_WAKE);
            updateNumberPickers(wakeUpTime);
        });

        wakeUpTime = 7*60;
        sleepTime = 23*60;

        int sleepId = getIntent().getExtras().getInt("id", -1);
        if (sleepId >= 0) {
            new Thread(() -> {
                Sleep sleep = MainActivity.getDatabase().sleepDAO().getSleep(sleepId);

                runOnUiThread(() -> {
                    wakeUpTime = sleep.endTime;
                    sleepTime = sleep.startTime;

                    binding.tvSleep.setText(TimeUtils.toTimeNotation(sleepTime));
                    binding.tvWakeUp.setText(TimeUtils.toTimeNotation(wakeUpTime));

                    updateNumberPickers(sleepTime);
                });
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.hasExtra("ringtoneId")) {
            prodId = data.getExtras().getInt("ringtoneId");

            new Thread(() -> {
                String name = MainActivity.getDatabase().dao().getRingtone(prodId).get(0).name;

                binding.tvRingName.setText(name);
            }).start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        binding.setTheme(MainActivity.getAppliedTheme());

        setContentView(binding.getRoot());

        if (getIntent().getExtras().getInt("type") == TYPE_SLEEP) {
            gotoSleepBranch();
        }
        else {
            int alarmId = getIntent().getExtras().getInt("id", -1);
            if (alarmId >= 0) {
                isUpdate = true;
                loadAlarmSettings(alarmId);
            }
        }

        binding.btnChangeRingtone.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, RingtoneActivity.class), 1);
        });

        binding.btnDiscard.setOnClickListener(v -> discardAlarm());
        binding.btnSave.setOnClickListener(v -> saveAlarm());

        binding.npHrs.setOnValueChangedListener(this::handlePickerValueChanged);
        binding.npMins.setOnValueChangedListener(this::handlePickerValueChanged);

        LayoutUtils.setSystemBarColor(getWindow(), binding.getTheme().getPrimary(), binding.getTheme().getPrimary());

        LayoutUtils.onBackPressed(this, getOnBackPressedDispatcher(), this::discardAlarm);
    }
}