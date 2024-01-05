package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;

import night.app.R;
import night.app.services.ChartBuilder;

public class WeekRecordFragment extends Fragment {
    private void loadBarChart(View view) {
        BarChart barchart = (BarChart) view.findViewById(R.id.barChart_week_record);

        String[] xLabel = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        Integer[] yRange = {0, 16};

        Integer[] data = {3,4,6,null,4,12,0};

        new ChartBuilder(barchart, xLabel, yRange, data);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_record, container, false);

        ((TextView) requireActivity().findViewById(R.id.tv_anal_main_info_title1))
                .setText(R.string.average_sleep);

        ((TextView) requireActivity().findViewById(R.id.tv_anal_main_info_title2))
                .setText(R.string.sleep_efficiency);
    loadBarChart(view);
        return view;
    }
}
