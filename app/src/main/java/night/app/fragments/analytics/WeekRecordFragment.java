package night.app.fragments.analytics;

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
import java.util.concurrent.TimeUnit;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.entities.Day;
import night.app.data.entities.Sleep;
import night.app.databinding.FragmentWeekRecordBinding;
import night.app.services.ChartBuilder;
import night.app.services.Sample;
import night.app.services.SleepData;
import night.app.utils.TimeUtils;

public class WeekRecordFragment extends Fragment {
    private FragmentWeekRecordBinding binding;
    private long startDate;
    private long endDate;

    private void setUpperPanelResult(String date, int score, long info1, double info2) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("date", date);
        bundle.putInt("score", score);
        bundle.putLong("info1", info1);
        bundle.putDouble("info2", info2);

        if (!isAdded()) return;
        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
    }

    private void loadBarChart(long startDate, long endDate) {
        new Thread(() -> {
            Day[] days = MainActivity.getDatabase().dayDAO().getDayRange(startDate, endDate);
            loadBarChart(days);
        }).start();
    }

    private void loadBarChart(Day[] days) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            if (days.length == 0) {
                setUpperPanelResult(TimeUtils.toDateString(startDate, endDate), 0, 0, 0);

                new ChartBuilder<>(binding.barChartWeekRecord, new Integer[] {})
                        .invalidate();
                return;
            }

            new Thread(() -> {
                SleepData[] sleepData = new SleepData[days.length];
                for (int i=0; i < days.length; i++) {
                    if (days[i] != null) {
                        sleepData[i] = new SleepData(MainActivity.getDatabase().sleepEventDAO().getByRange(days[i].date, days[i].startTime, days[i].endTime));

                    }
                }

                int score = 0;
                for (SleepData data : sleepData) {
                    if (data != null) score += data.getScore();
                }

                long sleepInMills = 0;
                double sleepEfficiency = 0d;


                for (SleepData data : sleepData) {
                    if (data == null) continue;

                    double efficiency = data.getSleepEfficiency();
                    if (efficiency >= 0) sleepEfficiency += efficiency;

                    sleepInMills += data.getConfidenceDuration(50, 100);
                }

                Integer[] sleepHrs = new Integer[7];
                Arrays.fill(sleepHrs, 0);

                for (int i=0; i < days.length; i++) {
                    if (sleepData[i] == null) continue;;

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(days[i].date);

                    // starting at Sunday (index = 0)
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                    int sleepInHrs = (int) TimeUnit.MILLISECONDS.toHours(sleepInMills);
                    // to distinguish between no data and less sleep
                    sleepHrs[dayOfWeek-1] = Math.max(sleepInHrs, 1);

                }

                int finalScore = score;
                long finalSleepInMills = sleepInMills;
                double finalSleepEfficiency = sleepEfficiency;
                requireActivity().runOnUiThread(() -> {
                    new ChartBuilder<>(binding.barChartWeekRecord, sleepHrs)
                            .invalidate();


                    setUpperPanelResult(
                            TimeUtils.toDateString(startDate, endDate),
                            finalScore / days.length,
                            finalSleepInMills / days.length,
                            finalSleepEfficiency / days.length
                    );
                });
            }).start();
        });
    }

    private void toPreviousWeek() {
        endDate = TimeUtils.dayAdd(startDate, -1);
        startDate = TimeUtils.dayAdd(startDate, -7);

        if (startDate < TimeUtils.dayAdd(TimeUtils.getTodayAtMidNight(), -29)) {
            startDate = TimeUtils.dayAdd(TimeUtils.getTodayAtMidNight(), -29);
        }

        loadBarChart(startDate, endDate);

        if (getActivity() == null) return;
        getActivity().findViewById(R.id.iv_right).setVisibility(View.VISIBLE);

        if (TimeUtils.dayAdd(endDate, -7) < TimeUtils.dayAdd(TimeUtils.getTodayAtMidNight(), -30)) {
            getActivity().findViewById(R.id.iv_left).setVisibility(View.GONE);
            return;
        }
        getActivity().findViewById(R.id.iv_right).setVisibility(View.VISIBLE);
    }

    private void toNextWeek() {
        startDate = TimeUtils.dayAdd(endDate, 1);
        endDate = TimeUtils.dayAdd(endDate, 7);

        if (endDate > TimeUtils.getTodayAtMidNight()) endDate = TimeUtils.getTodayAtMidNight();

        loadBarChart(startDate, endDate);

        if (TimeUtils.dayAdd(startDate, 7) > TimeUtils.getTodayAtMidNight()) {
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

            if (mode == AnalyticsPageFragment.MODE_SAMPLE) {
                List<Day> days = Sample.getDay();
                startDate = TimeUtils.dayAdd(startDate, -7);
                endDate = 0;

//                loadBarChart(days);

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
        endDate = calendar.getTimeInMillis();

        calendar.add(Calendar.DATE, -calendar.get(Calendar.DAY_OF_WEEK)+1);
        startDate = calendar.getTimeInMillis();;

        loadBarChart(startDate, endDate);

        return binding.getRoot();
    }
}
