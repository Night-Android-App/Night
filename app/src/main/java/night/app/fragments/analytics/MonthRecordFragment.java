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
import night.app.adapters.DayItemAdapter;
import night.app.data.entities.Day;
import night.app.databinding.FragmentMonthRecordBinding;
import night.app.services.Sample;
import night.app.utils.TimeUtils;

public class MonthRecordFragment extends Fragment {
    private FragmentMonthRecordBinding binding;

    private void setAdapter(List<Day> dayList) {
        binding.rvItems.setAdapter(
                new DayItemAdapter((AppCompatActivity) getActivity(), dayList)
        );

        requireView().findViewById(R.id.ll_no_Data)
                .setVisibility(dayList.size() > 0 ? View.INVISIBLE : View.VISIBLE);
    }

    public void setUpperPanelResult(String date, int score, double info1, double info2) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("date", date);
        bundle.putInt("score", score);
        bundle.putDouble("info1", info1);
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
            dayList = Sample.getDay();
            endDate = 0;
        }
        else {
            dayList = MainActivity.getDatabase().dayDAO().getAllDay();
            endDate = TimeUtils.getTodayAtMidNight();
        }

        long startedDate = TimeUtils.dayAdd(endDate, -29);

        getActivity().runOnUiThread(() -> {
            if (dayList.size() == 0) {
                setUpperPanelResult(TimeUtils.toDateString(startedDate, endDate), 0, 0, 0);
                return;
            }

            double sleepScore = 0, sleepSeconds = 0, sleepEfficiency = 0;

            for (int i=0; i < dayList.size(); i++) {
//                SleepData data = new SleepData("{}");
//
//                sleepScore += data.getScore();
//
//                double efficiency = data.getSleepEfficiency();
//                if (efficiency >= 0) sleepEfficiency += efficiency;
//
//                int totalSleep = data.getTotalSleep();
//                if (totalSleep >= 0) sleepSeconds += totalSleep;
            }

            setUpperPanelResult(
                    TimeUtils.toDateString(startedDate, endDate),
                    (int) sleepScore / dayList.size(),
                    sleepSeconds / dayList.size(),
                    sleepEfficiency / dayList.size()
            );

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
