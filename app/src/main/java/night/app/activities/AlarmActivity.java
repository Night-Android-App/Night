package night.app.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import night.app.data.entities.Alarm;
import night.app.data.entities.Ringtone;
import night.app.data.entities.Sleep;
import night.app.data.dao.AlarmDAO;
import night.app.databinding.ActivityAlarmBinding;
import night.app.fragments.dialogs.ConfirmDialog;
import night.app.services.AlarmReceiver;
import night.app.utils.AlarmSchedule;
import night.app.services.NotificationReceiver;
import night.app.utils.DoNotDisturb;
import night.app.utils.LayoutUtils;
import night.app.utils.DatetimeUtils;
import night.app.utils.Notification;

public class AlarmActivity extends AppCompatActivity {
    public final static int TYPE_SLEEP = 0;
    public final static int TYPE_ALARM = 1;

    private int type = TYPE_ALARM;

    private final static int NP_SLEEP = 0;
    private final static int NP_WAKE = 1;

    private long sleepTime = TimeUnit.HOURS.toMillis(23);;
    private long wakeUpTime = TimeUnit.HOURS.toMillis(7);;
    private Integer prodId = null;

    private boolean isUpdate = false;

    private Alarm alarm;

    private ActivityAlarmBinding binding;

    private void discardAlarm() {
        new ConfirmDialog("Discard changed settings", "Are you sure?", (dialog) -> {
            dialog.dismiss();
            setResult(RESULT_OK);

            Toast.makeText(this, "Discard Alarm Settings", Toast.LENGTH_SHORT).show();
            finish();
        }).show(getSupportFragmentManager(), null);
    }

    private void insertAlarm() {
        AlarmDAO alarmDAO = MainActivity.getDatabase().alarmDAO();

        new Thread(() -> {
            alarmDAO.create(wakeUpTime, binding.swMission.isChecked() ? 1 : 0, prodId);

            scheduleAlarm(alarmDAO.getLast().id);

            setResult(RESULT_OK);
            finish();
        }).start();

        Toast.makeText(this, "Created Alarm", Toast.LENGTH_SHORT).show();
    }

    private void updateExistedAlarm() {
        AlarmDAO alarmDAO = MainActivity.getDatabase().alarmDAO();
        int enableMission = binding.swMission.isChecked() ? 1 : 0;

        new Thread(() -> alarmDAO.update(alarm.id, wakeUpTime, enableMission, prodId)).start();

        Toast.makeText(this, "Updated Alarm", Toast.LENGTH_SHORT).show();

        scheduleAlarm(alarm.id);

        setResult(RESULT_OK);
        finish();
    }

    private void scheduleAlarm(int id) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(DatetimeUtils.getClosestDateTime(wakeUpTime));

        new AlarmSchedule(getApplicationContext()).post(id, calendar.getTimeInMillis());
    }

    private void saveAlarm() {
        Notification.requestPermission(this);

        if (type == TYPE_SLEEP) {
            new Thread(() -> {
                int isMission = binding.swMission.isChecked() ? 1 : 0;
                int isDnd = binding.swDnd.isChecked() ? 1 : 0;
                MainActivity.getDatabase().sleepDAO().insertUpdate(sleepTime, wakeUpTime, isMission, isDnd, prodId);

                NotificationReceiver.schedule(getApplicationContext(), sleepTime);
            }).start();

            setResult(RESULT_OK);
            finish();
        }
        else if (type == TYPE_ALARM) {
            if (isUpdate) updateExistedAlarm();
            else insertAlarm();
        }
    }

    private void handlePickerValueChanged(NumberPicker np, int old, int newValue) {
        long hrValue = TimeUnit.HOURS.toMillis(binding.npHrs.getValue());
        long minValue = TimeUnit.MINUTES.toMillis(binding.npMins.getValue());

        if (binding.getNpType() == NP_WAKE) {
            wakeUpTime = hrValue + minValue;
            binding.tvWakeUp.setText(DatetimeUtils.toTimeNotation(wakeUpTime));
        }
        else if (binding.getNpType() == NP_SLEEP) {
            sleepTime = hrValue + minValue;
            binding.tvSleep.setText(DatetimeUtils.toTimeNotation(sleepTime));
        }
    }

    private void updateNumberPickers(long ms) {
        binding.npHrs.setValue((int) TimeUnit.MILLISECONDS.toHours(ms));
        binding.npMins.setValue((int) (TimeUnit.MILLISECONDS.toMinutes(ms) - binding.npHrs.getValue() * 60));
    }

    private void initSleepSettingStyles() {
        type = TYPE_SLEEP;

        binding.llToggleTime.setVisibility(View.VISIBLE);
        binding.swDnd.setVisibility(View.VISIBLE);

        binding.tvTitle.setText("Sleep Settings");
    }

    private void loadAlarmSettings(int alarmId) {
        new Thread(() -> {
            alarm = MainActivity.getDatabase().alarmDAO().getById(alarmId);
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

        binding.setNpType(NP_SLEEP);

        binding.llSleep.setOnClickListener(v -> {
            binding.setNpType(NP_SLEEP);
            updateNumberPickers(sleepTime);
        });

        binding.llWakeUp.setOnClickListener(v -> {
            binding.setNpType(NP_WAKE);
            updateNumberPickers(wakeUpTime);
        });

        binding.swDnd.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) DoNotDisturb.requestPermission(this);
        });

        new Thread(() -> {
            Sleep sleep = MainActivity.getDatabase().sleepDAO().getSleep();

            if (sleep != null) {
                sleepTime = sleep.startTime;
                wakeUpTime = sleep.endTime;
                binding.swMission.setChecked(sleep.enableMission == 1);
                binding.swDnd.setChecked(sleep.enableDND == 1);
            }

            binding.tvSleep.setText(DatetimeUtils.toTimeNotation(sleepTime));
            binding.tvWakeUp.setText(DatetimeUtils.toTimeNotation(wakeUpTime));

            updateNumberPickers(sleepTime);
        }).start();
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

    private void gotoAlarmBranch() {
        int alarmId = getIntent().getExtras().getInt("id", -1);

        binding.setNpType(NP_WAKE);

        // set current time to number picker (more convenient for users)
        wakeUpTime = DatetimeUtils.getMsOfToday();

        long hours = TimeUnit.MILLISECONDS.toHours(wakeUpTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(wakeUpTime) - hours * 60L;

        binding.npHrs.setValue((int) hours);
        binding.npMins.setValue((int) minutes);

        if (alarmId >= 0) {
            isUpdate = true;
            loadAlarmSettings(alarmId);
        }
    }

    @Override
    protected void onStop() {
        setResult(RESULT_OK);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        discardAlarm();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        binding.setTheme(MainActivity.getAppliedTheme());

        setContentView(binding.getRoot());
        LayoutUtils.setSystemBarColor(getWindow(), binding.getTheme().getPrimary(), binding.getTheme().getPrimary());


        binding.btnChangeRingtone.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, RingtoneActivity.class), 1);
        });

        binding.btnDiscard.setOnClickListener(v -> discardAlarm());
        binding.btnSave.setOnClickListener(v -> saveAlarm());

        binding.npHrs.setOnValueChangedListener(this::handlePickerValueChanged);
        binding.npMins.setOnValueChangedListener(this::handlePickerValueChanged);


        if (getIntent().getExtras().getInt("type") == TYPE_SLEEP) gotoSleepBranch();
        else gotoAlarmBranch();
    }
}