package night.app.adapters;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import night.app.activities.AlarmActivity;
import night.app.activities.MainActivity;
import night.app.data.entities.Alarm;
import night.app.databinding.ItemAlarmBinding;
import night.app.fragments.clocks.AlarmFragment;
import night.app.services.AlarmSchedule;
import night.app.utils.ColorUtils;
import night.app.utils.TimeUtils;

public class AlarmViewHolder extends RecyclerView.ViewHolder {
    private AlarmAdapter adapter;
    private ItemAlarmBinding binding;

    public final static int STATUS_NORMAL = 0;
    public final static int STATUS_SELECT = 1;

    private int status = STATUS_NORMAL;
    private Fragment fr;
    private Alarm alarm;

    private void showDetailDialog() {
        Intent intent = new Intent(adapter.activity, AlarmActivity.class);
        intent.putExtra("type", AlarmActivity.TYPE_ALARM);
        intent.putExtra("id", alarm.id);

        ((AlarmFragment) fr).mStartForResult.launch(intent);
    }

    private void updateAlarmStatus(int id) {
        new Thread(() -> {
            MainActivity.getDatabase().alarmDAO()
                    .setEnable(id, binding.swAlarmEnable.isChecked() ? 1 : 0);

            if (binding.swAlarmEnable.isChecked()) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, alarm.endTime - calendar.get(Calendar.HOUR) * 60 - calendar.get(Calendar.MINUTE));

                new AlarmSchedule(adapter.activity.getApplicationContext())
                        .setRingtone(alarm.ringtoneId)
                        .post(alarm.id, calendar.getTimeInMillis());
            }
            else {
                new AlarmSchedule(adapter.activity.getApplicationContext())
                        .setRingtone(alarm.ringtoneId)
                        .cancel(alarm.id);
            }

        }).start();
    }

    public void loadData(Alarm alarm) {
        this.alarm = alarm;

        binding.tvTime.setText(TimeUtils.toTimeNotation((int) TimeUnit.MILLISECONDS.toSeconds(alarm.endTime)));
        binding.swAlarmEnable.setChecked(alarm.enableAlarm == 1);

        binding.swAlarmEnable.setOnClickListener(v -> updateAlarmStatus(alarm.id));
    }

    private boolean handleOnLongClickItem(View view) {
        if (status == STATUS_NORMAL) {
            setSelectedMode();
        }
        else if (status == STATUS_SELECT) {
            setNormalMode();
        }

        return true;
    }

    public void setNormalMode() {
        adapter.unselectAlarm(getAdapterPosition());
        binding.clItemAlarm.setBackgroundTintList(ColorStateList.valueOf(binding.getTheme().getSurface()));
        status = STATUS_NORMAL;
    }

    public void setSelectedMode() {
        adapter.selectAlarm(getAdapterPosition());
        binding.clItemAlarm.setBackgroundTintList(ColorStateList.valueOf(
                ColorUtils.darken(binding.getTheme().getSurface(), 0.9f)
        ));
        status = STATUS_SELECT;

        adapter.mode = AlarmAdapter.MODE_SELECT;
    }

    private void handleOnClickItem(View view) {
        if (adapter.mode == AlarmAdapter.MODE_NORMAL) {
            showDetailDialog();
        }
        else if (adapter.mode == AlarmAdapter.MODE_SELECT) {
            if (status == STATUS_NORMAL) {
                setSelectedMode();
            }
            else if (status == STATUS_SELECT) {
                setNormalMode();
            }
        }
    }

    public AlarmViewHolder(AlarmAdapter adapter, ItemAlarmBinding binding, Fragment fr) {
        super(binding.getRoot());
        binding.setTheme(MainActivity.getAppliedTheme());

        this.adapter = adapter;
        this.binding = binding;
        this.fr = fr;

        binding.clItemAlarm.setOnClickListener(this::handleOnClickItem);
        binding.clItemAlarm.setOnLongClickListener(this::handleOnLongClickItem);
    }
}
