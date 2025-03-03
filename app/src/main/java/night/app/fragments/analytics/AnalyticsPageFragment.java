package night.app.fragments.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.entities.Theme;
import night.app.databinding.FragmentAnalyticsPageBinding;
import night.app.utils.LayoutUtils;
import night.app.utils.DatetimeUtils;

public class AnalyticsPageFragment extends Fragment {
    private FragmentAnalyticsPageBinding binding;

    public static final int MODE_NORMAL = 0;
    public static final int MODE_SAMPLE = 1;
    private int mode = MODE_NORMAL;

    private Bundle createBundleForMode() {
        Bundle bundle = new Bundle();
        bundle.putInt("mode", mode);

        return bundle;
    }

    private void switchDetailsPage(Class<? extends Fragment> fr) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fr_anal_details, fr, createBundleForMode())
                .commit();
    }

    private void deleteOldRecords() {
        new Thread(() -> {
            long endDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            long startDate = endDate - 29*24*60*60;

            MainActivity.getDatabase().dayDAO().deleteOldDays(startDate);
        }).start();
    }

    private void handleUpperPanelResult(int type, String date, int score, long info1, double info2) {
        binding.tvAnalMainInfoTitle1.setText(type == 0 ? "FELL ASLEEP" : "AVG SLEEP");

        binding.tvAnalDate.setText(date);

        if (score >= 0) {
            binding.tvAnalMainScoreData.setText(String.valueOf(Math.round(score)));
        }
        else {
            binding.tvAnalMainScoreData.setText("N/A");
        }

        String info1String = info1 >= 0 ? DatetimeUtils.toHrMinString(info1) : "N/A";
        binding.tvAnalMainInfoData1.setText(info1String);

        String info2String = info2 >= 0 ? Math.round(info2 * 100) + "%" : "N/A";
        binding.tvAnalMainInfoData2.setText(info2String);
    }

    private void setUpperPanelResultListener() {
        requireActivity().getSupportFragmentManager()
                .setFragmentResultListener("updateAnalytics", this, (String key, Bundle bundle) -> {
                    requireActivity().runOnUiThread(() -> {
                        handleUpperPanelResult(
                                bundle.getInt("type"),
                                bundle.getString("date"),
                                bundle.getInt("score", -1),
                                bundle.getLong("info1", -1),
                                bundle.getDouble("info2", -1)
                        );
                    });
                });
    }

    private void setOnTabSelectedListener() {
        Theme theme = binding.getTheme();
        binding.tabAnal.setTabTextColors(theme.getOnPrimaryVariant(), theme.getOnPrimary());

        LayoutUtils.onSelected(binding.tabAnal, tab -> {
            Class<? extends Fragment> fragmentClass = switch (tab.getPosition()) {
                case 0 -> DayRecordFragment.class;
                case 1 -> WeekRecordFragment.class;
                default -> MonthRecordFragment.class;
            };

            switchDetailsPage(fragmentClass);
        });
    }

    private void setStyleForNormalMode() {
        mode = MODE_NORMAL;
        binding.tvTips.setText("You don't have any data. See with a sample");
        binding.ivTips.setImageResource(R.drawable.ic_chevron_right);
    }

    private void setStyleForSampleMode() {
        mode = MODE_SAMPLE;
        binding.tvTips.setText("Exit the sample mode");
        binding.ivTips.setImageResource(R.drawable.ic_exits);
    }

    private void toggleMode() {
        switch (mode) {
            case MODE_NORMAL -> setStyleForSampleMode();
            case MODE_SAMPLE -> setStyleForNormalMode();
        }

        Fragment fr = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fr_anal_details);
        if (fr != null) switchDetailsPage(fr.getClass());
        System.out.println("?");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_analytics_page, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        setOnTabSelectedListener();
        setUpperPanelResultListener();

        deleteOldRecords();

        new Thread(() -> {
            if (MainActivity.getDatabase().dayDAO().getRecent().size() > 0) {
                binding.llTips.setVisibility(View.GONE);
            }
        }).start();

        binding.llTips.setOnClickListener(v -> toggleMode());

        requireActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fr_anal_details, DayRecordFragment.class, createBundleForMode())
                .commit();

        return binding.getRoot();
    }
}
