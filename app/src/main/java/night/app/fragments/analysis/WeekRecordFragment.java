package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;

import java.util.Arrays;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.FragmentWeekRecordBinding;
import night.app.services.ChartBuilder;

public class WeekRecordFragment extends Fragment {
    FragmentWeekRecordBinding binding;

    private void updateUpperPanel(String info1, String info2) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);

        bundle.putString("info1", info1);
        bundle.putString("info2", info2);

        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
    }

    private void loadBarChart(View view) {
        BarChart barchart = view.findViewById(R.id.barChart_week_record);

        String[] xLabel = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        Integer[] data = {3,4,6,0,4,12,1};

        new ChartBuilder(barchart, binding.getTheme(), Arrays.asList(xLabel), data);

        int avgSleep = 0;
        for (Integer sleepHours : data) {
            avgSleep += sleepHours;
        }
        avgSleep /= data.length;
        updateUpperPanel(avgSleep + "h", "N/A");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_week_record, container, false);
        binding.setTheme(((MainActivity) requireActivity()).theme);

        loadBarChart(binding.getRoot());


        return binding.getRoot();
    }
}
