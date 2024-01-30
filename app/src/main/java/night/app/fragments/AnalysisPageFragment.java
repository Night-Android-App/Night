package night.app.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.Theme;
import night.app.databinding.FragmentAnalysisPageBinding;
import night.app.fragments.analysis.DayRecordFragment;
import night.app.fragments.analysis.MonthRecordFragment;
import night.app.fragments.analysis.WeekRecordFragment;
import night.app.fragments.settings.BackupConfigFragment;
import night.app.fragments.settings.OthersConfigFragment;
import night.app.fragments.settings.SleepConfigFragment;

public class AnalysisPageFragment extends Fragment {
    FragmentAnalysisPageBinding binding;

    private Class<? extends Fragment> getFragmentById(int id) {
        if (id == R.id.tab_anal_day) return DayRecordFragment.class;
        if (id == R.id.tab_anal_week) return WeekRecordFragment.class;
        return MonthRecordFragment.class;
    }

    public void switchSettingsType(View view) {
        Class<? extends Fragment> fragmentClass = getFragmentById(view.getId());

        // get the instance of the display fragment
        Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fr_anal_details);

        // no action if user click the active tab
        if (fragmentClass.isInstance(fragment)) return;

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fr_anal_details, fragmentClass, null)
                .commit();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_analysis_page, container, false);
        Theme theme = ((MainActivity) requireActivity()).theme;

        binding.setFragment(this);
        binding.setTheme(theme);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fr_anal_details, DayRecordFragment.class, null)
                .commit();


        binding.tabAnal.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Class<? extends Fragment> fragmentClass = switch (tab.getPosition()) {
                    case 0 -> DayRecordFragment.class;
                    case 1 -> WeekRecordFragment.class;
                    case 2 -> MonthRecordFragment.class;
                    default ->
                            throw new IllegalStateException("Unexpected value: " + tab.getPosition());
                };

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fr_anal_details, fragmentClass, null)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        binding.tabAnal.setSelectedTabIndicatorColor(theme.textContrast);
        binding.tabAnal.setTabTextColors(theme.textInactive, theme.textContrast);
        return binding.getRoot();
    }
}
