package night.app.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.Calendar;
import java.util.List;

import night.app.data.entities.Alarm;
import night.app.data.entities.Repeat;
import night.app.data.entities.Ringtone;
import night.app.data.entities.Sleep;
import night.app.data.dao.AlarmDAO;
import night.app.databinding.ActivityAlarmBinding;
import night.app.fragments.dialogs.ConfirmDialog;
import night.app.services.AlarmReceiver;
import night.app.services.AlarmSchedule;
import night.app.utils.LayoutUtils;
import night.app.utils.TimeUtils;

public class AlarmActivity extends AppCompatActivity {
    public final static int TYPE_SLEEP = 0;
    public final static int TYPE_ALARM = 1;

    private int type = TYPE_ALARM;


    private final static int NP_SLEEP = 0;
    private final static int NP_WAKE = 1;

    private int sleepTime;
    private int wakeUpTime;
    private Integer prodId = null;

    private boolean isUpdate = false;

    private Repeat repeat;
    private Alarm alarm;


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
            alarmDAO.updateAlarm(alarm.id, wakeUpTime, enableMission, prodId);
            finish();
        }).start();
    }

    private void scheduleAlarm(int id) {
        int delayInSeconds;
        if (TimeUtils.getTodayHrMin() > wakeUpTime) {
            delayInSeconds = (wakeUpTime + (23*60 - TimeUtils.getTodayHrMin()))*60 - Calendar.getInstance().get(Calendar.MINUTE) * 60;
        }
        else {
            delayInSeconds = (wakeUpTime - TimeUtils.getTodayHrMin())*60- Calendar.getInstance().get(Calendar.MINUTE)*60;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, wakeUpTime - calendar.get(Calendar.HOUR) * 60 - calendar.get(Calendar.MINUTE));

        new AlarmSchedule(getApplicationContext())
                .post(id, calendar.getTimeInMillis());

    }

    private void saveAlarm() {
        if (type == TYPE_SLEEP) {
            binding.swDnd.isChecked();

            new Thread(() -> {
                if (MainActivity.getDatabase().sleepDAO().getSleep() == null) {
                    MainActivity.getDatabase().sleepDAO().addSleep(
                            sleepTime, wakeUpTime,
                            binding.swMission.isChecked() ? 1 : 0, binding.swDnd.isChecked() ? 1 : 0,
                            prodId
                    );
                }
                else {
                    MainActivity.getDatabase().sleepDAO().updateSleep(
                            sleepTime, wakeUpTime,
                            binding.swMission.isChecked() ? 1 : 0, binding.swDnd.isChecked() ? 1 : 0,
                            prodId
                    );
                }
            }).start();

            setResult(RESULT_OK);
            finish();
        }
        else if (type == TYPE_ALARM) {
            AlarmDAO alarmDAO = MainActivity.getDatabase().alarmDAO();

            if (isUpdate) {
                updateExistedAlarm();
                return;
            }
            new Thread(() -> {
                int enableMission = binding.swMission.isChecked() ? 1 : 0;

                alarmDAO.createAlarm(wakeUpTime, enableMission, prodId);

                scheduleAlarm(alarmDAO.getLastAlarm().id);
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

        if (binding.getNpType() == NP_WAKE) {
            wakeUpTime = hrValue + minValue;
            binding.tvWakeUp.setText(TimeUtils.toTimeNotation(wakeUpTime*60));
        }
        else if (binding.getNpType() == NP_SLEEP) {
            sleepTime = hrValue + minValue;
            binding.tvSleep.setText(TimeUtils.toTimeNotation(sleepTime*60));
        }
    }

    private void updateNumberPickers(int minutes) {
        binding.npHrs.setValue(minutes / 60);
        binding.npMins.setValue(minutes - binding.npHrs.getValue() * 60);
    }

    private void initSleepSettingStyles() {
        type = TYPE_SLEEP;

        binding.llToggleTime.setVisibility(View.VISIBLE);
        binding.swDnd.setVisibility(View.VISIBLE);

        binding.tvTitle.setText("Sleep Settings");
    }

    private void loadAlarmSettings(int alarmId) {
        new Thread(() -> {
            alarm = MainActivity.getDatabase().alarmDAO().getAlarm(alarmId);
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

        binding.tvRepeat.setVisibility(View.GONE);
        binding.llRepeat.setVisibility(View.GONE);

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
                Sleep sleep = MainActivity.getDatabase().sleepDAO().getSleep();

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

    private void setRepeat(View view, boolean isActive) {
        ((LinearLayout) view).getChildAt(0).setBackgroundTintList(
                isActive
                        ? ColorStateList.valueOf(binding.getTheme().getAccent())
                        : ColorStateList.valueOf(binding.getTheme().getOnPrimaryVariant())
        );

    }

    private void gotoAlarmBranch() {
        int alarmId = getIntent().getExtras().getInt("id", -1);
        if (alarmId >= 0) {
            isUpdate = true;
            loadAlarmSettings(alarmId);
        }

        binding.llSu.setOnClickListener(v -> {
            repeat.day1 = repeat.day1 == 1 ? 0 : 1;
            setRepeat(v, repeat.day1 == 1);
        });

        binding.llMo.setOnClickListener(v -> {
            repeat.day2 = repeat.day2 == 1 ? 0 : 1;
            setRepeat(v, repeat.day2 == 1);
        });

        binding.llTu.setOnClickListener(v -> {
            repeat.day3 = repeat.day3 == 1 ? 0 : 1;
            setRepeat(v, repeat.day3 == 1);
        });

        binding.llWe.setOnClickListener(v -> {
            repeat.day4 = repeat.day4 == 1 ? 0 : 1;
            setRepeat(v, repeat.day4 == 1);
        });

        binding.llTh.setOnClickListener(v -> {
            repeat.day5 = repeat.day5 == 1 ? 0 : 1;
            setRepeat(v, repeat.day5 == 1);
        });

        binding.llFr.setOnClickListener(v -> {
            repeat.day6 = repeat.day6 == 1 ? 0 : 1;
            setRepeat(v, repeat.day6 == 1);
        });

        binding.llSa.setOnClickListener(v -> {
            repeat.day7 = repeat.day7 == 1 ? 0 : 1;
            setRepeat(v, repeat.day7 == 1);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        discardAlarm();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        binding.setTheme(MainActivity.getAppliedTheme());

        if (repeat == null) repeat = new Repeat();

        setContentView(binding.getRoot());

        if (getIntent().getExtras().getInt("type") == TYPE_SLEEP) {
            gotoSleepBranch();
        }
        else {
            gotoAlarmBranch();
        }

        binding.btnChangeRingtone.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, RingtoneActivity.class), 1);
        });

        binding.btnDiscard.setOnClickListener(v -> discardAlarm());
        binding.btnSave.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                saveAlarm();
            }
            else {
                new ConfirmDialog("Permission required", "Notification", (dialog) -> {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // for Android 5-7
                    // intent.putExtra("app_package", getPackageName());
                    // intent.putExtra("app_uid", getApplicationInfo().uid);

                    intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

                    startActivity(intent);
                    dialog.dismiss();
                }).show(getSupportFragmentManager(), null);
            }
        });

        binding.npHrs.setOnValueChangedListener(this::handlePickerValueChanged);
        binding.npMins.setOnValueChangedListener(this::handlePickerValueChanged);

        LayoutUtils.setSystemBarColor(getWindow(), binding.getTheme().getPrimary(), binding.getTheme().getPrimary());
    }
}