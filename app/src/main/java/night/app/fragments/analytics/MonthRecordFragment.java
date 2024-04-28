package night.app.fragments.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.adapters.DayAdapter;
import night.app.data.entities.Day;
import night.app.data.entities.SleepEvent;
import night.app.databinding.FragmentMonthRecordBinding;
import night.app.utils.DaySample;
import night.app.utils.SleepAnalyser;
import night.app.utils.DatetimeUtils;

public class MonthRecordFragment extends Fragment {
    private FragmentMonthRecordBinding binding;

    private void setAdapter(List<Day> dayList) {
        binding.rvItems.setAdapter(
                new DayAdapter((AppCompatActivity) getActivity(), dayList)
        );

        requireView().findViewById(R.id.ll_no_Data)
                .setVisibility(dayList.size() > 0 ? View.INVISIBLE : View.VISIBLE);
    }

    public void setUpperPanelResult(String date, int score, long info1, double info2) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("date", date);
        bundle.putInt("score", score);
        bundle.putLong("info1", info1);
        bundle.putDouble("info2", info2);

        if (!isAdded()) return;
        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
    }

    private void loadData() {
        if (getActivity() == null || getArguments() == null) return;

        List<Day> dayList;
        long endDate;

        int mode = getArguments().getInt("mode", 0);
        if (mode == AnalyticsPageFragment.MODE_SAMPLE) {
            dayList = DaySample.getDay();
            endDate = 0;
        }
        else {
            dayList = MainActivity.getDatabase().dayDAO().getAll();
            endDate = DatetimeUtils.getTodayAtMidNight();
        }

        long startedDate = DatetimeUtils.dayAdd(endDate, -29);

        if (dayList.size() == 0) {
            setUpperPanelResult(DatetimeUtils.toDateString(startedDate, endDate), 0, 0, 0);
            return;
        }

            double sleepScore = 0, sleepInMills = 0, sleepEfficiency = 0;

            for (int i=0; i < dayList.size(); i++) {
                Day day = dayList.get(i);

                SleepEvent[] events = MainActivity.getDatabase().sleepEventDAO()
                        .get(day.date, day.startTime, day.endTime);

                SleepAnalyser data = new SleepAnalyser(events);

                sleepScore += data.getScore();
                sleepEfficiency += data.getSleepEfficiency();

                sleepInMills += data.getConfidenceDuration(50, 100);
            }

            setUpperPanelResult(
                    DatetimeUtils.toDateString(startedDate, endDate),
                    (int) sleepScore / dayList.size(),
                    (long) (sleepInMills / dayList.size()),
                    sleepEfficiency / dayList.size()
            );

            requireActivity().runOnUiThread(() -> {
                setAdapter(dayList);
            });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_month_record, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        binding.rvItems.setLayoutManager(new LinearLayoutManager(requireActivity()));

        new Thread(this::loadData).start();

        getActivity().findViewById(R.id.iv_left).setVisibility(View.GONE);
        getActivity().findViewById(R.id.iv_right).setVisibility(View.GONE);

        return binding.getRoot();
    }
}
