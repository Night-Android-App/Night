package night.app.fragments;

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

public class AnalysisPageFragment extends Fragment {
    FragmentAnalysisPageBinding binding;

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

        binding.tabAnal.setSelectedTabIndicatorColor(theme.onPrimary);
        binding.tabAnal.setTabTextColors(theme.onPrimaryVariant, theme.onPrimary);
        return binding.getRoot();
    }
}
