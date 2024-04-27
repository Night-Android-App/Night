package night.app.fragments.clocks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import night.app.R;
import night.app.activities.AlarmActivity;
import night.app.activities.MainActivity;
import night.app.adapters.AlarmAdapter;
import night.app.data.entities.Alarm;
import night.app.data.entities.Sleep;
import night.app.databinding.FragmentAlarmBinding;
import night.app.utils.DatetimeUtils;

public class AlarmFragment extends Fragment {
    public FragmentAlarmBinding binding;

    public ActivityResultLauncher<Intent> mStartForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode()== Activity.RESULT_OK) {
                    loadAlarmList();
                }
            });

    private void loadAlarmList() {
        new Thread(() -> {
            List<Alarm> alarmList = MainActivity.getDatabase().alarmDAO().getAll();

            requireActivity().runOnUiThread(() -> {
                AlarmAdapter adapter = new AlarmAdapter((AppCompatActivity) requireActivity(), this, alarmList);
                binding.rvAlarm.setAdapter(adapter);
            });
        }).start();

    }

    private void handleOnClickDiscard(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle("Discard selected alarms?")
                .setMessage("This action cannot be reversed.")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (dialog, which) -> {
                    AlarmAdapter adapter = (AlarmAdapter) binding.rvAlarm.getAdapter();
                    if (adapter != null) {
                        int totalAlarmsDiscarded = adapter.discardSelectedAlarms();

                        String desc = totalAlarmsDiscarded + " alarm(s) be discarded";
                        Toast.makeText(requireActivity(), desc, Toast.LENGTH_SHORT).show();
                    }
                });

        builder.show();
    }

    private void updateSleepMsg(String upper, String lower) {
        Bundle bundle = new Bundle();
        bundle.putString("upperMsg", upper);
        bundle.putString("lowerMsg", lower);

        if (isAdded()) {
            getParentFragmentManager().setFragmentResult("updateSleepInfo", bundle);
        }
    }

    @Override @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        binding.rvAlarm.setLayoutManager(new LinearLayoutManager(requireActivity()));
        loadAlarmList();

        new Thread(() -> {
            Sleep entity = MainActivity.getDatabase().sleepDAO().getSleep();

            if (entity != null) {
                new Thread(() -> {
                    updateSleepMsg(
                            "We will notify you at " + DatetimeUtils.toTimeNotation((int) TimeUnit.MILLISECONDS.toSeconds(entity.startTime)),
                            "and wake up at " + DatetimeUtils.toTimeNotation((int) TimeUnit.MILLISECONDS.toSeconds(entity.endTime))
                    );
                }).start();
                return;
            }
            updateSleepMsg("Sleep time has not been set", "Press here to configure");
        }).start();


        binding.ibAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AlarmActivity.class);

            intent.putExtra("type", AlarmActivity.TYPE_ALARM);
            mStartForResult.launch(intent);
        });

        binding.ibTrash.setOnClickListener(this::handleOnClickDiscard);

        return binding.getRoot();
    }
}
