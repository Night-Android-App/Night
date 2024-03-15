package night.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private FragmentAnalysisPageBinding binding;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_SAMPLE = 1;
    private int status = STATUS_NORMAL;

    private void deleteOldRecords() {
        new Thread(() -> {
            long endDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            long startDate = endDate - 29*24*60*60;

            MainActivity.getDatabase().dao().deleteOldDays(startDate);
        }).start();
    }

    private void handleUpperPanelResult(int type, String date, int score, double info1, double info2) {
        binding.tvAnalMainInfoTitle1.setText(type == 0 ? "FELL ASLEEP" : "AVG SLEEP");

        binding.tvAnalDate.setText(date);

        if (score > 0) {
            binding.tvAnalMainScoreData.setText(String.valueOf(Math.round(score)));
        }
        else {
            binding.tvAnalMainScoreData.setText("N/A");
        }

        String info1String = info1 > 0 ? SleepData.toHrMinString((int) Math.round(info1)) : "N/A";
        binding.tvAnalMainInfoData1.setText(info1String);

        String info2String = info2 > 0 ? Math.round(info2 * 100) + "%" : "N/A";
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

                Bundle bundle = new Bundle();
                bundle.putInt("status", status);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fr_anal_details, fragmentClass, bundle)
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
        binding.setTheme(MainActivity.getAppliedTheme());

        initTabLayout();
        setUpperPanelResultListener();

        deleteOldRecords();

        new Thread(() -> {
            if (MainActivity.getDatabase().dao().getRecentDay().size() > 0) {
                binding.llTips.setVisibility(View.GONE);
            }
        }).start();

        binding.llTips.setOnClickListener(v -> {
            if (status == STATUS_NORMAL) {
                status = 1;
                binding.tvTips.setText("Exit the sample mode");
                binding.ivTips.setImageResource(R.drawable.ic_exits);
            }
            else if (status == STATUS_SAMPLE) {
                status = 0;
                binding.tvTips.setText("You don't have any data. See with a sample");
                binding.ivTips.setImageResource(R.drawable.ic_chevron_right);
            }

            Bundle bundle = new Bundle();
            bundle.putInt("status", status);

            Class<? extends Fragment> fr = getParentFragmentManager()
                    .findFragmentById(R.id.fr_anal_details).getClass();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fr_anal_details, fr, bundle)
                    .commit();
        });

        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fr_anal_details, DayRecordFragment.class, bundle)
                .commit();

        return binding.getRoot();
    }
}
