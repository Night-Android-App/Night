package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.Day;
import night.app.databinding.FragmentDayRecordBinding;
import night.app.services.ChartBuilder;

public class DayRecordFragment extends Fragment {
    FragmentDayRecordBinding binding;

    public static int getTimeDifference(int prevTime, int laterTime) {
        if (prevTime > laterTime) {
            return 24 * 60 + laterTime - prevTime;
        }
        return laterTime - prevTime;
    }

    private String secondsToTimeStringFormat(int seconds) {
        int hours = (int) Math.floor(seconds/60f);
        int minutes = seconds % 60;

        return LocalTime.of(hours, minutes).toString();
    }

    private String intToDateStringFormat(int timestamp) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        return formatter.format(zonedDateTime);
    }

    private void updateUpperPanel(String date, String info1, String info2) {
        Bundle bundle = new Bundle();

        bundle.putInt("type", 0);
        bundle.putString("date", date);

        bundle.putString("info1", info1);
        bundle.putString("info2", info2);

        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
    }

    private void loadLineChart() {
        LineChart lineChart = binding.lineChartDayRecord;

        new Thread(() -> {
            Day day = ((MainActivity) requireActivity()).appDatabase.dao().getDayByID(1).get(0);

            requireActivity().runOnUiThread(() -> {
                JSONObject sleepData = null;

                try { sleepData = new JSONObject(day.sleep); }
                catch (JSONException e) { return; }

                List<Integer> data = new ArrayList<>();
                List<Integer> xLabel = new ArrayList<>();

                for (Iterator<String> it = sleepData.keys(); it.hasNext();) {
                    String key = it.next();
                    xLabel.add(Integer.parseInt(key));

                    data.add(sleepData.optInt(key));
                }

                int timeDifference = getTimeDifference(xLabel.get(0), xLabel.get(xLabel.size()-1));
                binding.tvInBed.setText(secondsToTimeStringFormat(timeDifference));

                List<String> nXLabel = new ArrayList<>();
                for (Integer i : xLabel) {
                    nXLabel.add(secondsToTimeStringFormat(i));
                }

                updateUpperPanel(intToDateStringFormat(day.date), "N/A", "N/A");
                new ChartBuilder(lineChart, binding.getTheme(), nXLabel, data);

                binding.tvAnalDream.setText(day.dream);
            });

        }).start();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_day_record, container, false);
        binding.setTheme(((MainActivity) requireActivity()).theme);

        loadLineChart();

        return binding.getRoot();
    }
}
