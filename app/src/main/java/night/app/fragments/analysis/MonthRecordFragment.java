package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.adapters.DayItemAdapter;
import night.app.data.Day;
import night.app.services.SleepData;

public class MonthRecordFragment extends Fragment {

    public void setUpperPanelResult(String date, int score, double info1, double info2) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("date", date);
        bundle.putInt("score", score);
        bundle.putDouble("info1", info1);
        bundle.putDouble("info2", info2);

        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
    }

    private void loadData() {
        MainActivity activity = (MainActivity) requireActivity();

        List<Day> dayList = MainActivity.getDatabase().dao().getAllDay();

        requireActivity().runOnUiThread(() -> {
            long today = System.currentTimeMillis()/ 1000;
            long aMonthBefore = today - 29*24*60*60;

            if (dayList.size() == 0) {
                setUpperPanelResult(SleepData.toDateString(aMonthBefore, today), 0, 0, 0);
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


            int availableDay = dayList.size() == 0 ? 1 : dayList.size();
            setUpperPanelResult(
                    SleepData.toDateString(aMonthBefore, today),
                    (int) sleepScore / availableDay,
                    sleepSeconds / availableDay,
                    sleepEfficiency / availableDay
            );

            RecyclerView view = (RecyclerView) requireView();
            view.setAdapter(new DayItemAdapter(activity, dayList));
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycle_view, container, false);

        ((RecyclerView) view).setLayoutManager(new LinearLayoutManager(requireActivity()));

        new Thread(this::loadData).start();

        requireActivity().findViewById(R.id.iv_left).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.iv_right).setVisibility(View.GONE);

        return view;
    }
}
