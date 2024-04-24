package night.app.fragments.clocks;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import night.app.R;
import night.app.activities.AlarmActivity;
import night.app.activities.MainActivity;
import night.app.data.entities.Sleep;
import night.app.databinding.FragmentClockPageBinding;
import night.app.fragments.clocks.AlarmFragment;
import night.app.fragments.clocks.NapFragment;
import night.app.utils.LayoutUtils;
import night.app.utils.TimeUtils;

public class ClockPageFragment extends Fragment {
    public FragmentClockPageBinding binding;

    private void setSleepMsgResultListener() {
        getChildFragmentManager().setFragmentResultListener("updateSleepInfo", this, (requestKey, bundle) -> {
            String upperMsg = bundle.getString("upperMsg");
            String lowerMsg = bundle.getString("lowerMsg");

            if (upperMsg != null && lowerMsg != null) {
                binding.upperMsg.setText(upperMsg);
                binding.lowerMsg.setText(lowerMsg);
            }
        });
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    new Thread(() -> {
                        Sleep entity = MainActivity.getDatabase().sleepDAO().getSleep();
                        if (entity != null) {
                            requireActivity().runOnUiThread(() -> {
                                showExistedSleepRecord(entity);
                            });
                        }
                    }).start();
                }
            });

    private void showExistedSleepRecord(Sleep entity) {
        binding.upperMsg.setText("We will notify you at " + TimeUtils.toTimeNotation(entity.startTime*60));
        binding.lowerMsg.setText("and wake up at " +TimeUtils.toTimeNotation(entity.endTime*60));
    }

    private void setOnTabSelectedListener() {
        LayoutUtils.onSelected(binding.tabClock, tab -> {
            Class<? extends Fragment> fr =
                    tab.getPosition() == 0 ? AlarmFragment.class : NapFragment.class;

            if (tab.getPosition() == 0) {
                binding.ivSleep.setImageResource(R.drawable.ic_tab_analysis);
            }
            else {
                binding.ivSleep.setImageResource(R.drawable.ic_snooze);
            }

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fr_clock_details, fr, null)
                    .commit();
        });
    }

    @Override @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clock_page, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        setOnTabSelectedListener();
        setSleepMsgResultListener();


        getChildFragmentManager().beginTransaction()
                .add(R.id.fr_clock_details, AlarmFragment.class, null)
                .commit();

        binding.clClockTop.setOnClickListener(v -> {
            if (getChildFragmentManager().findFragmentById(R.id.fr_clock_details) instanceof AlarmFragment) {
                Intent intent = new Intent(getContext(), AlarmActivity.class);

                intent.putExtra("type", AlarmActivity.TYPE_SLEEP);
                resultLauncher.launch(intent);
            }
        });

        return binding.getRoot();
    }
}
