package night.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.FragmentClockPageBinding;
import night.app.fragments.clocks.AlarmFragment;
import night.app.fragments.clocks.NapFragment;
import night.app.utils.LayoutUtils;

public class ClockPageFragment extends Fragment {
    private FragmentClockPageBinding binding;

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

    private void setOnTabSelectedListener() {
        binding.tabClock.addOnTabSelectedListener(LayoutUtils.getOnTabSelectedListener(tab -> {
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
        }));
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

        return binding.getRoot();
    }
}
