package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;
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

        if (sleepData != null) {
            bundle.putInt("score", Math.round(sleepData.getScore()));
            bundle.putDouble("info1", sleepData.getFellAsleepTime());
            bundle.putDouble("info2", sleepData.getSleepEfficiency());
        }
        else {
            bundle.putInt("score", 0);
            bundle.putDouble("info1", 0);
            bundle.putDouble("info2", 0);
        }

        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
    }

    private void loadDay(@Nullable Integer date) {
        new Thread(() -> {
            AppDAO dao = MainActivity.getDatabase().dao();
            List<Day> dayList = date == null ? dao.getRecentDay() : dao.getDayByDate(date);

            requireActivity().runOnUiThread(() -> loadDay(dayList));
        }).start();
    }

    private void loadDay(List<Day> dayList) {
        Day day;
        if (dayList.size() > 0) {
            day = dayList.get(0);
        }
        else {
            day = new Day();
            day.date = (int) (System.currentTimeMillis()/ 1000);
        }

        SleepData sleepData = day.sleep == null ? null : new SleepData(day.sleep);
        setUpperPanelResult(SleepData.toDateString(day.date, true), sleepData);


        if (day.sleep == null) {
            binding.tvLightSleep.setText("0m");
            binding.tvDeepSleep.setText("0m");
            binding.tvInBed.setText("0m");

            new ChartBuilder(binding.lineChartDayRecord, null, null)
                    .setTheme(binding.getTheme())
                    .invalidate();
            return;
        };

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_day_record, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        setLoadDayResultListener();
        loadDay((Integer) null);

        return binding.getRoot();
    }
}
