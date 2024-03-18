package night.app.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.Alarm;
import night.app.databinding.ItemAlarmBinding;
import night.app.databinding.ItemShopThemeBinding;
import night.app.utils.ColorUtils;
import night.app.utils.TimeUtils;

public class AlarmViewHolder extends RecyclerView.ViewHolder {
    private AlarmAdapter adapter;
    private ItemAlarmBinding binding;

    public final static int STATUS_NORMAL = 0;
    public final static int STATUS_SELECT = 1;

    private int status = STATUS_NORMAL;

    private void showDetailDialog() {

    }

    private void updateAlarmStatus(int id) {
        new Thread(() -> {
            MainActivity.getDatabase().dao()
                    .updateAlarmEnabled(id, binding.swAlarmEnable.isChecked() ? 1 : 0);
        }).start();
    }

    public void loadData(Alarm alarm) {
        binding.tvTime.setText(TimeUtils.toTimeNotation(alarm.endTime * 60));
        binding.swAlarmEnable.setChecked(alarm.isAlarmEnabled == 1);

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

    public AlarmViewHolder(AlarmAdapter adapter, ItemAlarmBinding binding) {
        super(binding.getRoot());
        binding.setTheme(MainActivity.getAppliedTheme());

        this.adapter = adapter;
        this.binding = binding;

        binding.clItemAlarm.setOnClickListener(this::handleOnClickItem);
        binding.clItemAlarm.setOnLongClickListener(this::handleOnLongClickItem);
    }
}
