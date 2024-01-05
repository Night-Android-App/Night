package night.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import night.app.R;
import night.app.fragments.analysis.DayRecordFragment;
import night.app.fragments.analysis.MonthRecordFragment;
import night.app.fragments.analysis.WeekRecordFragment;

public class AnalysisPageFragment extends Fragment {
    public void switchSettingsType(View view) {
        Class<? extends Fragment> fragmentClass;

        if (view.getId() == R.id.tab_anal_day) {
            fragmentClass = DayRecordFragment.class;
        }
        else if (view.getId() == R.id.tab_anal_week) {
            fragmentClass = WeekRecordFragment.class;
        }
        else if (view.getId() == R.id.tab_anal_month) {
            fragmentClass = MonthRecordFragment.class;
        }
        else return;

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fr_anal_details, fragmentClass, null).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fr_anal_details, DayRecordFragment.class, null)
                .commit();

        view.findViewById(R.id.tab_anal_day).setOnClickListener(this::switchSettingsType);
        view.findViewById(R.id.tab_anal_week).setOnClickListener(this::switchSettingsType);
        view.findViewById(R.id.tab_anal_month).setOnClickListener(this::switchSettingsType);


        return view;
    }
}
