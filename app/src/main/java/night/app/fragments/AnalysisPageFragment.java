package night.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.FragmentAnalysisPageBinding;
import night.app.fragments.analysis.DayRecordFragment;
import night.app.fragments.analysis.MonthRecordFragment;
import night.app.fragments.analysis.WeekRecordFragment;
import night.app.services.SleepData;

public class AnalysisPageFragment extends Fragment {
    FragmentAnalysisPageBinding binding;

    private void deleteOldRecords() {
        new Thread(() -> {
            MainActivity activity = (MainActivity) requireActivity();

            long endDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            long startDate = endDate - 29*24*60*60;

            activity.appDatabase.dao().deleteOldDays(startDate);
        }).start();
    }

    private void handleUpperPanelResult(int type, String date, int score, double info1, double info2) {
        binding.tvAnalMainInfoTitle1.setText(type == 0 ? "FELL ASLEEP" : "AVG SLEEP");

        binding.tvAnalDate.setText(date);

        String scoreString = score >= 0 ? String.valueOf(Math.round(score)) : "N/A";
        binding.tvAnalMainScoreData.setText(scoreString);


        String info1String = info1 >= 0 ? SleepData.toHrMinString((int) Math.round(info1)) : "N/A";
        binding.tvAnalMainInfoData1.setText(info1String);

        String info2String = info2 >= 0 ? Math.round(info2 * 100) + "%" : "N/A";
        binding.tvAnalMainInfoData2.setText(info2String);
    }

    private void setUpperPanelResultListener() {
        getParentFragmentManager()
                .setFragmentResultListener("updateAnalytics", this, (String key, Bundle bundle) -> {
                    requireActivity().runOnUiThread(() -> {
                        handleUpperPanelResult(
                                bundle.getInt("type"),
                                bundle.getString("date"),
                                bundle.getInt("score", -1),
                                bundle.getDouble("info1", -1),
                                bundle.getDouble("info2", -1)
                        );
                    });
                });
    }

    private void initTabLayout() {
        binding.tabAnal.setSelectedTabIndicatorColor(binding.getTheme().getOnPrimary());
        binding.tabAnal.setTabTextColors(binding.getTheme().getOnPrimaryVariant(), binding.getTheme().getOnPrimary());

        binding.tabAnal.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Class<? extends Fragment> fragmentClass = switch (tab.getPosition()) {
                    case 0 -> DayRecordFragment.class;
                    case 1 -> WeekRecordFragment.class;
                    default -> MonthRecordFragment.class;
                };

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fr_anal_details, fragmentClass, null)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_analysis_page, container, false);
        binding.setTheme(((MainActivity) requireActivity()).theme);

        initTabLayout();
        setUpperPanelResultListener();

        deleteOldRecords();

        requireActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fr_anal_details, DayRecordFragment.class, null)
                .commit();

        return binding.getRoot();
    }
}
