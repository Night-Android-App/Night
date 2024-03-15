package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.Day;
import night.app.databinding.FragmentWeekRecordBinding;
import night.app.fragments.AnalysisPageFragment;
import night.app.services.ChartBuilder;
import night.app.services.Sample;
import night.app.services.SleepData;
import night.app.utils.TimeUtils;

public class WeekRecordFragment extends Fragment {
    private FragmentWeekRecordBinding binding;
    private int startDate;
    private int endDate;

    private void setUpperPanelResult(String date, int score, double info1, double info2) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("date", date);
        bundle.putInt("score", score);
        bundle.putDouble("info1", info1);
        bundle.putDouble("info2", info2);

        if (!isAdded()) return;
        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
    }

    private void loadBarChart(long startDate, long endDate) {
        new Thread(() -> {
            List<Day> dayList = MainActivity.getDatabase().dao().getDayRange(startDate, endDate);
            loadBarChart(dayList);
        }).start();
    }

    private void loadBarChart(List<Day> dayList) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            if (dayList == null || dayList.size() == 0) {
                setUpperPanelResult(TimeUtils.toDateString(startDate, endDate), 0, 0, 0);

                new ChartBuilder<>(binding.barChartWeekRecord, new Integer[] {})
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

            new ChartBuilder<>(binding.barChartWeekRecord, sleepHrs)
                    .setTheme(binding.getTheme())
                    .invalidate();


            setUpperPanelResult(
                    TimeUtils.toDateString(startDate, endDate),
                    score / dayList.size(),
                    sleepSeconds / dayList.size(),
                    sleepEfficiency / dayList.size()
            );
        });
    }

    private void toPreviousWeek() {
        endDate = TimeUtils.dayAdd(startDate, -1);
        startDate = TimeUtils.dayAdd(startDate, -7);

        if (startDate < TimeUtils.dayAdd(TimeUtils.getToday(), -29)) {
            startDate = TimeUtils.dayAdd(TimeUtils.getToday(), -29);
        }

        loadBarChart(startDate, endDate);

        if (getActivity() == null) return;
        getActivity().findViewById(R.id.iv_right).setVisibility(View.VISIBLE);

        if (TimeUtils.dayAdd(endDate, -7) < TimeUtils.dayAdd(TimeUtils.getToday(), -30)) {
            getActivity().findViewById(R.id.iv_left).setVisibility(View.GONE);
            return;
        }
        getActivity().findViewById(R.id.iv_right).setVisibility(View.VISIBLE);
    }

    private void toNextWeek() {
        startDate = TimeUtils.dayAdd(endDate, 1);
        endDate = TimeUtils.dayAdd(endDate, 7);

        if (endDate > TimeUtils.getToday()) endDate = TimeUtils.getToday();

        loadBarChart(startDate, endDate);

        if (TimeUtils.dayAdd(startDate, 7) > TimeUtils.getToday()) {
            requireActivity().findViewById(R.id.iv_right).setVisibility(View.GONE);
            return;
        }
        requireActivity().findViewById(R.id.iv_left).setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_week_record, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        if (getActivity() == null) return binding.getRoot();

        ImageView ivLeft = getActivity().findViewById(R.id.iv_left);
        ImageView ivRight = getActivity().findViewById(R.id.iv_right);

        if (getArguments() != null) {
            int mode = getArguments().getInt("mode", 0);

            if (mode == AnalysisPageFragment.MODE_SAMPLE) {
                List<Day> days = Sample.getDay();
                startDate = TimeUtils.dayAdd(startDate, -7);
                endDate = 0;

                loadBarChart(days);

                ivLeft.setVisibility(View.GONE);
                ivRight.setVisibility(View.GONE);
                return binding.getRoot();
            }
        }

        ivLeft.setVisibility(View.VISIBLE);
        ivLeft.setOnClickListener(v -> toPreviousWeek());

        ivRight.setVisibility(View.GONE);
        ivRight.setOnClickListener(v -> toNextWeek());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        endDate = (int) (calendar.getTimeInMillis() / 1000);

        calendar.add(Calendar.DATE, -calendar.get(Calendar.DAY_OF_WEEK)+1);
        startDate = (int) (calendar.getTimeInMillis() / 1000);;

        loadBarChart(startDate, endDate);

        return binding.getRoot();
    }
}
