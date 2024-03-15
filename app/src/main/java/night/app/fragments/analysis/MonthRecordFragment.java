package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.adapters.DayItemAdapter;
import night.app.data.Day;
import night.app.fragments.AnalysisPageFragment;
import night.app.services.Sample;
import night.app.services.SleepData;
import night.app.utils.TimeUtils;

public class MonthRecordFragment extends Fragment {
    private void setAdapter(List<Day> dayList) {
        if (getView() != null) {
            RecyclerView view = (RecyclerView) getView();
            if (getActivity() instanceof AppCompatActivity) {
                view.setAdapter(new DayItemAdapter((AppCompatActivity) getActivity(), dayList));
            }
        }
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
        int endDate;

        int mode = getArguments().getInt("mode", 0);
        if (mode == AnalysisPageFragment.MODE_SAMPLE) {
            dayList = Sample.getDay();
            endDate = 0;
        }
        else {
            dayList = MainActivity.getDatabase().dao().getAllDay();
            endDate = TimeUtils.getToday();
        }

        int startedDate = TimeUtils.dayAdd(endDate, -29);

        getActivity().runOnUiThread(() -> {
            if (dayList.size() == 0) {
                setUpperPanelResult(TimeUtils.toDateString(startedDate, endDate), 0, 0, 0);
                return;
            }

            double sleepScore = 0, sleepSeconds = 0, sleepEfficiency = 0;

            for (int i=0; i < dayList.size(); i++) {
                SleepData data = new SleepData(dayList.get(i).sleep);

                sleepScore += data.getScore();

                double efficiency = data.getSleepEfficiency();
                if (efficiency >= 0) sleepEfficiency += efficiency;

                int totalSleep = data.getTotalSleep();
                if (totalSleep >= 0) sleepSeconds += totalSleep;
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
        View view = inflater.inflate(R.layout.fragment_recycle_view, container, false);

        ((RecyclerView) view).setLayoutManager(new LinearLayoutManager(requireActivity()));

        new Thread(this::loadData).start();

        if (getActivity() != null) {
            getActivity().findViewById(R.id.iv_left).setVisibility(View.GONE);
            getActivity().findViewById(R.id.iv_right).setVisibility(View.GONE);
        }

        return view;
    }
}
