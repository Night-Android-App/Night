package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import night.app.R;

public class MonthRecordFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month_record, container, false);

        ((TextView) requireActivity().findViewById(R.id.tv_anal_main_info_title1))
                .setText(R.string.average_sleep);
        ((TextView) requireActivity().findViewById(R.id.tv_anal_main_info_title2))
                .setText(R.string.sleep_efficiency);

        return view;
    }
}
