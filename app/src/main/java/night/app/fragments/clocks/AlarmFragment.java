package night.app.fragments.clocks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.adapters.AlarmAdapter;
import night.app.data.Alarm;
import night.app.databinding.FragmentAlarmBinding;
import night.app.fragments.dialogs.ConfirmDialog;

public class AlarmFragment extends Fragment {
    private FragmentAlarmBinding binding;

    private void showSleepSettings() {

    }

    private void loadAlarmList() {
        new Thread(() -> {
            List<Alarm> alarmList = MainActivity.getDatabase().dao().getAllAlarms();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    AlarmAdapter adapter = new AlarmAdapter(alarmList);
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

    @Override @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false);

        binding.rvAlarm.setLayoutManager(new LinearLayoutManager(requireActivity()));
        loadAlarmList();

        updateSleepMsg("Sleep time has not been set", "Press here to configure");

        binding.ibTrash.setOnClickListener(this::handleOnClickDiscard);
        return binding.getRoot();
    }
}
