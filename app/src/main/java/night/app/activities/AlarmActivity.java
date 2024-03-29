package night.app.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.Calendar;
import java.util.List;

import night.app.data.Alarm;
import night.app.data.Ringtone;
import night.app.data.Sleep;
import night.app.data.Theme;
import night.app.databinding.ActivityAlarmBinding;
import night.app.fragments.dialogs.ConfirmDialog;
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
    private int prodId = 0;

    private boolean isUpdate = false;


    private ActivityAlarmBinding binding;

    private void discardAlarm() {
        new ConfirmDialog("Discard changed settings", "Are you sure?", (dialog) -> {
            dialog.dismiss();
            finish();
        }).show(getSupportFragmentManager(), null);
    }

    private void saveAlarm() {

        if (type == TYPE_SLEEP) {
            binding.swDnd.isChecked();

        }
        else if (type == TYPE_ALARM) {

            if (isUpdate) {
                new Thread(() -> {
                    MainActivity.getDatabase().alarmDAO().updateAlarm(wakeUpTime,  binding.swMission.isChecked() ? 1 : 0, prodId);
                    finish();
                }).start();
            }
            else {
                new Thread(() -> {
                    MainActivity.getDatabase().alarmDAO().createAlarm(wakeUpTime,  binding.swMission.isChecked() ? 1 : 0, prodId);

                    finish();
                }).start();
            }
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

    private void setSleepWakeColor(int sleepBg, int wakeBg, int sleep, int wake) {
        binding.llSleep.setBackgroundTintList(ColorStateList.valueOf(sleepBg));
        binding.llWakeUp.setBackgroundTintList(ColorStateList.valueOf(wakeBg));

        binding.tvWakeUpTitle.setTextColor(wake);
        binding.tvWakeUp.setTextColor(wake);

        binding.tvSleepTitle.setTextColor(sleep);
        binding.tvSleep.setTextColor(sleep);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        binding.setTheme(MainActivity.getAppliedTheme());
        setContentView(binding.getRoot());


        if (getIntent().getExtras().getInt("type") == TYPE_SLEEP) {
            int sleepId = getIntent().getExtras().getInt("id", -1);

            binding.llToggleTime.setVisibility(View.VISIBLE);
            binding.swDnd.setVisibility(View.VISIBLE);

            type = TYPE_SLEEP;
            npTarget = NP_SLEEP;

            binding.tvTitle.setText("Sleep Settings");


            Theme theme = binding.getTheme();
            setSleepWakeColor(theme.getAccent(), theme.getSurfaceVariant(), theme.getOnPrimary(), theme.getOnPrimaryVariant());

            binding.llSleep.setOnClickListener(v -> {
                npTarget = NP_SLEEP;

                setSleepWakeColor(theme.getAccent(), theme.getSurfaceVariant(), theme.getOnPrimary(), theme.getOnPrimaryVariant());
                binding.npHrs.setValue((int) (sleepTime / 60));
                binding.npMins.setValue(sleepTime - binding.npHrs.getValue() * 60);
            });
            binding.llWakeUp.setOnClickListener(v -> {
                npTarget = NP_WAKE;
                setSleepWakeColor(theme.getSurfaceVariant(), theme.getAccent(), theme.getOnPrimaryVariant(), theme.getOnPrimary());
                binding.npHrs.setValue((int) (wakeUpTime / 60));
                binding.npMins.setValue(wakeUpTime - binding.npHrs.getValue() * 60);
            });

            binding.npMins.setOnValueChangedListener(this::handlePickerValueChanged);
            binding.npHrs.setOnValueChangedListener(this::handlePickerValueChanged);

            wakeUpTime = 7*60;
            sleepTime = 23*60;
            binding.npHrs.setValue((int) (sleepTime / 60));
            binding.npMins.setValue(sleepTime - binding.npHrs.getValue() * 60);

            if (sleepId >= 0) {
                new Thread(() -> {
                    Sleep sleep = MainActivity.getDatabase().sleepDAO().getSleep(sleepId);

                    runOnUiThread(() -> {
                        wakeUpTime = sleep.endTime;
                        sleepTime = sleep.startTime;

                        binding.tvSleep.setText(TimeUtils.toTimeNotation(sleepTime));
                        binding.tvWakeUp.setText(TimeUtils.toTimeNotation(wakeUpTime));

                        binding.npHrs.setValue((int) (sleepTime / 60));
                        binding.npMins.setValue(sleepTime - binding.npHrs.getValue() * 60);
                    });
                }).start();
            }

        }
        else {
            int alarmId = getIntent().getExtras().getInt("id", -1);
            if (alarmId >= 0) {
                isUpdate = true;
                new Thread(() -> {
                    Alarm alarm = MainActivity.getDatabase().alarmDAO().getAlarm(alarmId);
                    prodId = alarm.ringtoneId;

                    List<Ringtone> ringtone = MainActivity.getDatabase().dao().getRingtone(alarm.ringtoneId);

                    runOnUiThread(() -> {
                        binding.swMission.setChecked(alarm.enableMission == 1);
                        if (ringtone.size() == 1) {
                            binding.tvRingName.setText(ringtone.get(0).name);
                        }

                        wakeUpTime = alarm.endTime;
                        binding.npHrs.setValue((int) (alarm.endTime / 60));
                        binding.npMins.setValue(alarm.endTime - binding.npHrs.getValue() * 60);
                    });
                }).start();
            }
        }


        binding.setTheme(MainActivity.getAppliedTheme());

        binding.btnChangeRingtone.setOnClickListener(v -> {
            startActivity(new Intent(this, RingtoneActivity.class));
        });

        binding.btnDiscard.setOnClickListener(v -> discardAlarm());
        binding.btnSave.setOnClickListener(v -> saveAlarm());

        binding.npHrs.setOnValueChangedListener(this::handlePickerValueChanged);
        binding.npMins.setOnValueChangedListener(this::handlePickerValueChanged);

        getWindow().setStatusBarColor(binding.getTheme().getPrimary());
        getWindow().setNavigationBarColor(binding.getTheme().getPrimary());
    }
}