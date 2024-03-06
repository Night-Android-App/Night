package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.AppDAO;
import night.app.data.Day;
import night.app.databinding.FragmentDayRecordBinding;
import night.app.services.ChartBuilder;
import night.app.services.SleepData;

public class DayRecordFragment extends Fragment {
    FragmentDayRecordBinding binding;

    private void setUpperPanelResult(String date, SleepData sleepData) {
        Bundle bundle = new Bundle();

        bundle.putInt("type", 0);
        bundle.putString("date", date);

        bundle.putInt("score", Math.round(sleepData.getScore()));
        bundle.putDouble("info1", sleepData.getFellAsleepTime());
        bundle.putDouble("info2", sleepData.getSleepEfficiency());

        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
    }

    private void loadDay(@Nullable Integer date) {
        MainActivity activity = (MainActivity) requireActivity();

        new Thread(() -> {
            AppDAO dao = activity.appDatabase.dao();
            List<Day> dayList = date == null ? dao.getRecentDay() : dao.getDayByDate(date);

            activity.runOnUiThread(() -> loadDay(dayList));
        }).start();
    }

    private void loadDay(List<Day> dayList) {
        Day day = dayList.size() > 0 ? dayList.get(0) : new Day();

        SleepData sleepData = new SleepData(day.sleep);

        setUpperPanelResult(SleepData.toDateString(day.date, true), sleepData);

        int totalLightSleep = sleepData.getTotalSecondsByConfidence(50, 75);
        binding.tvLightSleep.setText(
                SleepData.toHrMinString(totalLightSleep)
        );

        int totalDeepSleep = sleepData.getTotalSecondsByConfidence(75, 101);
        binding.tvDeepSleep.setText(
                SleepData.toHrMinString(totalDeepSleep)
        );

        binding.tvInBed.setText(SleepData.toHrMinString(sleepData.getTotalSleep()));
        binding.tvAnalDream.setText(day.dream == null ? "No record" : day.dream);

        Integer[] data = sleepData.getConfidences();
        new ChartBuilder(binding.lineChartDayRecord, SleepData.toHrMinString(sleepData.getTimelines()), data)
                .setTheme(binding.getTheme())
                .invalidate();
    }

    private void setLoadDayResultListener() {
        getParentFragmentManager()
                .setFragmentResultListener("loadDay", this, (String key, Bundle bundle) -> {
                    loadDay(bundle.getInt("date"));
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_day_record, container, false);
        binding.setTheme(activity.theme);

        setLoadDayResultListener();
        loadDay((Integer) null);

        return binding.getRoot();
    }
}
