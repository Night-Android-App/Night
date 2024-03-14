package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.AppDAO;
import night.app.data.Day;
import night.app.databinding.FragmentWeekRecordBinding;
import night.app.fragments.AnalysisPageFragment;
import night.app.services.ChartBuilder;
import night.app.services.SleepData;

public class WeekRecordFragment extends Fragment {
    private FragmentWeekRecordBinding binding;
    private long startDate;
    private long endDate;

    private void setUpperPanelResult(String date, int score, double info1, double info2) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("date", date);
        bundle.putInt("score", score);
        bundle.putDouble("info1", info1);
        bundle.putDouble("info2", info2);

        if (isAdded()) {
            getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
        }
    }

    private void loadBarChart(long startDate, long endDate) {
        new Thread(() -> {
            List<Day> dayList = MainActivity.getDatabase().dao().getDayRange(startDate, endDate);
            loadBarChart(dayList);
        }).start();
    }

    private void loadBarChart(List<Day> dayList) {
        requireActivity().runOnUiThread(() -> {
            if (dayList == null || dayList.size() == 0) {
                setUpperPanelResult(SleepData.toDateString(startDate, endDate), 0, 0, 0);

                new ChartBuilder(binding.barChartWeekRecord, new Integer[] {})
                        .setTheme(binding.getTheme())
                        .invalidate();
                return;
            }


            SleepData[] sleepData = SleepData.dayListToSleepDataArray(dayList);

            int score = Arrays.stream(sleepData).mapToInt(SleepData::getScore).sum();

            int sleepSeconds = 0;
            double sleepEfficiency = 0d;

            for (SleepData data : sleepData) {
                double efficiency = data.getSleepEfficiency();
                if (efficiency >= 0) sleepEfficiency += efficiency;

                int totalSleep = data.getTotalSleep();
                if (totalSleep >= 0) sleepSeconds += totalSleep;
            }

            Integer[] sleepHrs = new Integer[7];
            Arrays.fill(sleepHrs, 0);

            for (int i=0; i < dayList.size(); i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dayList.get(i).date * 1000);

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                sleepHrs[dayOfWeek == 7 ? 0 : dayOfWeek+1] = sleepData[i].getTotalSleep() / 60;
            }

            new ChartBuilder(binding.barChartWeekRecord, sleepHrs)
                    .setTheme(binding.getTheme())
                    .invalidate();


            int availableDay = dayList.size() == 0 ? 1 : dayList.size();
            setUpperPanelResult(
                    SleepData.toDateString(startDate, endDate),
                    score / availableDay,
                    sleepSeconds / availableDay,
                    sleepEfficiency / availableDay
            );
        });
    }

    private void toPreviousWeek() {
        endDate = startDate - 24*60*60;
        startDate -= 7*24*60*60;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        if (startDate < calendar.getTimeInMillis() / 1000 - 29*24*60*60) {
            startDate = calendar.getTimeInMillis() / 1000 - 29*24*60*60;
        }

        loadBarChart(startDate, endDate);
        requireActivity().findViewById(R.id.iv_right).setVisibility(View.VISIBLE);

        if (endDate - 7*24*60*60 < (calendar.getTimeInMillis() / 1000 - 30*24*60*60)) {
            requireActivity().findViewById(R.id.iv_left).setVisibility(View.GONE);
        }
        else {
            requireActivity().findViewById(R.id.iv_right).setVisibility(View.VISIBLE);
        }
    }

    private void toNextWeek() {
        startDate = endDate + 24*60*60;
        endDate = endDate + 7*24*60*60;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        long todayInMillis = calendar.getTimeInMillis() / 1000;
        if (endDate > todayInMillis) endDate = todayInMillis;

        loadBarChart(startDate, endDate);

        if (startDate + 7*24*60*60 > todayInMillis) {
            requireActivity().findViewById(R.id.iv_right).setVisibility(View.GONE);
            return;
        }
        requireActivity().findViewById(R.id.iv_left).setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_week_record, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        ImageView ivLeft = requireActivity().findViewById(R.id.iv_left);
        ImageView ivRight = requireActivity().findViewById(R.id.iv_right);

        if (requireArguments().getInt("status", 0) == AnalysisPageFragment.STATUS_SAMPLE) {
            List<Day> days = new ArrayList<>();
            days.add(SleepData.getSampleDay());
            startDate = -7*24*60*60;
            endDate = 0;

            loadBarChart(days);

            ivLeft.setVisibility(View.GONE);
            ivRight.setVisibility(View.GONE);
            return binding.getRoot();
        }

        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setOnClickListener(v -> toPreviousWeek());

        ivRight.setVisibility(View.GONE);
        ivRight.setOnClickListener(v -> toNextWeek());

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        endDate = calendar.getTimeInMillis() / 1000;

        calendar.add(Calendar.DATE, -calendar.get(Calendar.DAY_OF_WEEK)+1);
        startDate = calendar.getTimeInMillis() / 1000;

        loadBarChart(startDate, endDate);

        return binding.getRoot();
    }
}
