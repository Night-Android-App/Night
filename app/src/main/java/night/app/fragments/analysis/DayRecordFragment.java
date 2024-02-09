package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.Day;
import night.app.databinding.FragmentDayRecordBinding;
import night.app.fragments.dialogs.DreamDialog;
import night.app.services.ChartBuilder;
import night.app.services.DateTimeFormatter;

public class DayRecordFragment extends Fragment {
    FragmentDayRecordBinding binding;

    private void loadLineChart() {
        LineChart lineChart = binding.lineChartDayRecord;

        new Thread(() -> {
            Day day = ((MainActivity) requireActivity()).appDatabase.dao().getDayByID(1).get(0);

            JSONObject sleepData = null;

            try { sleepData = new JSONObject(day.sleep); }
            catch (JSONException e) { return; }

            List<Integer> data = new ArrayList<>();
            List<Integer> xLabel = new ArrayList<>();

            for (Iterator<String> it = sleepData.keys(); it.hasNext(); ) {
                String key = it.next();
                xLabel.add(Integer.parseInt(key));

                data.add(sleepData.optInt(key));
            }

            if (xLabel.get(0) > xLabel.get(xLabel.size()-1)) {
                binding.tvInBed.setText(
                    DateTimeFormatter.convertIntTime(
                        DateTimeFormatter.getTimeDifference(
                            xLabel.get(0),
                            xLabel.get(xLabel.size()-1)
                        )
                    )
                );
            }
            else {
                binding.tvInBed.setText(String.valueOf(xLabel.get(xLabel.size()-1) - xLabel.get(0)));
            }

            List<String> nXLabel = new ArrayList<>();
            for (Integer i : xLabel) {
                nXLabel.add(DateTimeFormatter.convertIntTime(i));
            }

            Bundle bundle = new Bundle();
            bundle.putInt("type", 0);
            bundle.putString("info1", nXLabel.get(0));
            bundle.putString("info2", nXLabel.get(nXLabel.size()-1));
            getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);

            new ChartBuilder(lineChart, binding.getTheme(), nXLabel, data);
        }).start();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_day_record, container, false);
        binding.setTheme(((MainActivity) requireActivity()).theme);

        ((TextView) requireActivity().findViewById(R.id.tv_anal_main_info_title1))
                .setText(R.string.sleep_start);

        ((TextView) requireActivity().findViewById(R.id.tv_anal_main_info_title2))
                .setText(R.string.sleep_end);

        loadLineChart();

        return binding.getRoot();
    }
}
