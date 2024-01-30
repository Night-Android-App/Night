package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import night.app.R;
import night.app.databinding.FragmentDayRecordBinding;
import night.app.services.ChartBuilder;

public class DayRecordFragment extends Fragment {
    FragmentDayRecordBinding binding;

    private void loadLineChart() {
        LineChart lineChart = binding.getRoot().findViewById(R.id.lineChart_day_record);

        Integer[] data = {32,98, 53, 49, 0};

        String[] xLabel = {"23:42", "01:00", "04:00", "06:00", "07:00"};

        Integer[] yRange = {0, 100};

        new ChartBuilder(lineChart, xLabel, yRange, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_day_record, container, false);

        ((TextView) requireActivity().findViewById(R.id.tv_anal_main_info_title1))
                .setText(R.string.sleep_start);

        ((TextView) requireActivity().findViewById(R.id.tv_anal_main_info_title2))
                .setText(R.string.sleep_end);

        loadLineChart();
        return binding.getRoot();
    }
}
