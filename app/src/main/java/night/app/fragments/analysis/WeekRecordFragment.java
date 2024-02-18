package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;

import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.AppDAO;
import night.app.data.Day;
import night.app.databinding.FragmentWeekRecordBinding;
import night.app.services.ChartBuilder;
import night.app.services.SleepData;

public class WeekRecordFragment extends Fragment {
    FragmentWeekRecordBinding binding;

    private void setUpperPanelResult(String date, int score, double info1, double info2) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("date", date);
        bundle.putInt("score", score);
        bundle.putDouble("info1", info1);
        bundle.putDouble("info2", info2);

        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
    }

    private List<Day> getWeekRecord(long todayDate) {
        AppDAO dao = ((MainActivity) requireActivity()).appDatabase.dao();

        List<Day> dayList = dao.getDayRange(todayDate-6*24*60*60, todayDate);
        return dayList;
    }

    private void loadBarChart(long startDate, long endDate, List<Day> dayList) {
        requireActivity().runOnUiThread(() -> {
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

            new ChartBuilder<BarChart>(binding.barChartWeekRecord, sleepHrs)
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_week_record, container, false);
        binding.setTheme(((MainActivity) requireActivity()).theme);

        new Thread(() -> {
            long today = Instant.now().toEpochMilli() / 1000;

            List<Day> dayList = getWeekRecord(today);
            if (dayList.size() == 0) {
                dayList.add(new Day());
                today = dayList.get(0).date;
            }

            long sixDayBefore = today - 6*24*60*60;

            loadBarChart(sixDayBefore, today, dayList);
        }).start();

        return binding.getRoot();
    }
}
