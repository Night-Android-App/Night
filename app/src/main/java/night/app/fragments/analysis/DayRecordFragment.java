package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.AppDAO;
import night.app.data.Day;
import night.app.databinding.FragmentDayRecordBinding;
import night.app.fragments.AnalysisPageFragment;
import night.app.services.ChartBuilder;
import night.app.services.SleepData;

public class DayRecordFragment extends Fragment {
    private FragmentDayRecordBinding binding;
    private long date;

    private void setUpperPanelResult(String date, SleepData sleepData) {
        Bundle bundle = new Bundle();

        bundle.putInt("type", 0);
        bundle.putString("date", date);

        if (sleepData != null) {
            bundle.putInt("score", Math.round(sleepData.getScore()));
            bundle.putDouble("info1", sleepData.getFellAsleepTime());
            bundle.putDouble("info2", sleepData.getSleepEfficiency());
        }

        if (isAdded()) {
            getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
        }
    }

    private void loadDay(long date) {
        new Thread(() -> {
            AppDAO dao = MainActivity.getDatabase().dao();
            List<Day> dayList = dao.getDayByDate(date);

            requireActivity().runOnUiThread(() -> loadDay(dayList));
        }).start();
    }

    private void loadDay(List<Day> dayList) {
        if (dayList.size() == 0) {
            binding.tvLightSleep.setText("N/A");
            binding.tvDeepSleep.setText("N/A");
            binding.tvInBed.setText("N/A");

            new ChartBuilder(binding.lineChartDayRecord, null, null)
                    .setTheme(binding.getTheme())
                    .invalidate();

            setUpperPanelResult(SleepData.toDateString(date, true), null);
            return;
        }

        Day day = dayList.get(0);
        SleepData sleepData = day.sleep == null ? null : new SleepData(day.sleep);
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

    private void loadSampleDay() {
        List<Day> days = new ArrayList<>();
        days.add(SleepData.getSampleDay());
        loadDay(days);
    }

    private void loadToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        date = calendar.getTimeInMillis() / 1000;
        loadDay(date);
    }

    private void setLoadDayResultListener() {
        getParentFragmentManager()
                .setFragmentResultListener("loadDay", this, (String key, Bundle bundle) -> {
                    int destDate = bundle.getInt("date", 0);

                    if (destDate == 0) {
                        loadSampleDay();
                        return;
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);

                    if (destDate == calendar.getTimeInMillis() / 1000) {
                        requireActivity().findViewById(R.id.iv_right).setVisibility(View.GONE);
                    }
                    else if (destDate == calendar.getTimeInMillis() / 1000 - 29*24*60*60) {
                        requireActivity().findViewById(R.id.iv_left).setVisibility(View.GONE);
                    }

                    loadDay(destDate);
                });
    }

    private void toPreviousDay() {
        date -= 24*60*60;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        loadDay(date);

        requireActivity().findViewById(R.id.iv_right).setVisibility(View.VISIBLE);

        if (date - 24*60*60 < (calendar.getTimeInMillis() / 1000 - 30*24*60*60)) {
            requireActivity().findViewById(R.id.iv_left).setVisibility(View.GONE);
            return;
        }
        requireActivity().findViewById(R.id.iv_right).setVisibility(View.VISIBLE);
    }

    private void toNextDay() {
        date += 24*60*60;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        loadDay(date);

        requireActivity().findViewById(R.id.iv_left).setVisibility(View.VISIBLE);

        if (date + 24*60*60 > calendar.getTimeInMillis() / 1000) {
            requireActivity().findViewById(R.id.iv_right).setVisibility(View.GONE);
            return;
        }
        requireActivity().findViewById(R.id.iv_right).setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_day_record, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        ImageView ivLeft = requireActivity().findViewById(R.id.iv_left);
        ImageView ivRight = requireActivity().findViewById(R.id.iv_right);

        if (requireArguments().getInt("status", 0) == AnalysisPageFragment.STATUS_SAMPLE) {
            loadSampleDay();
            ivLeft.setVisibility(View.GONE);
            ivRight.setVisibility(View.GONE);
            return binding.getRoot();
        }

        setLoadDayResultListener();

        loadToday();

        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setOnClickListener(v -> toPreviousDay());

        ivRight.setVisibility(View.GONE);
        ivRight.setOnClickListener(v -> toNextDay());

        return binding.getRoot();
    }
}
