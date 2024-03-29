package night.app.fragments.clocks;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.List;

import night.app.R;
import night.app.activities.AlarmActivity;
import night.app.activities.MainActivity;
import night.app.adapters.AlarmAdapter;
import night.app.data.Alarm;
import night.app.databinding.FragmentAlarmBinding;
import night.app.fragments.dialogs.ConfirmDialog;

public class AlarmFragment extends Fragment {
    public FragmentAlarmBinding binding;

    private MaterialTimePicker picker;
    private Calendar calendar;

    private void showSleepSettings() {

    }

    private void loadAlarmList() {
        new Thread(() -> {
            List<Alarm> alarmList = MainActivity.getDatabase().alarmDAO().getAllAlarms();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    AlarmAdapter adapter = new AlarmAdapter((AppCompatActivity) requireActivity(), alarmList);
                    binding.rvAlarm.setAdapter(adapter);
                });
            }
        }).start();
    }

    private void handleOnClickDiscard(View view) {
        new ConfirmDialog("Discard selected alarms", "This action cannot be reversed.", dialog -> {

            AlarmAdapter adapter = (AlarmAdapter) binding.rvAlarm.getAdapter();
            if (adapter != null) {
                int totalAlarmsDiscarded = adapter.discardSelectedAlarms();

                String dialogDesc = totalAlarmsDiscarded + " alarm(s) be discarded";
                dialog.replaceContent("Discard successes", dialogDesc, null);
            }
        }).show(getParentFragmentManager(), null);
    }

    private void updateSleepMsg(String upper, String lower) {
        Bundle bundle = new Bundle();
        bundle.putString("upperMsg", upper);
        bundle.putString("lowerMsg", lower);

        if (isAdded()) {
            getParentFragmentManager().setFragmentResult("updateSleepInfo", bundle);
        }
    }

    private void showTimePicker(View view) {
        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
            calendar.set(Calendar.MINUTE, picker.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

        });

        picker.show(getParentFragmentManager(), "timePicker");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "testReminderChannel";
            String description = "Night Alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("timePicker", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        binding.rvAlarm.setLayoutManager(new LinearLayoutManager(requireActivity()));
        loadAlarmList();

        updateSleepMsg("Sleep time has not been set", "Press here to configure");


        binding.ibAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AlarmActivity.class);

            intent.putExtra("type", AlarmActivity.TYPE_ALARM);
            startActivity(intent);
        });

        binding.ibTrash.setOnClickListener(this::handleOnClickDiscard);
        return binding.getRoot();
    }
}
