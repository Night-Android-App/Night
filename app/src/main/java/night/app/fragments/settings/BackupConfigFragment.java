package night.app.fragments.settings;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.PreferenceViewModel;
import night.app.databinding.FragmentBackupConfigBinding;

public class BackupConfigFragment extends Fragment {
    FragmentBackupConfigBinding binding;
    private ColorStateList getSwitchThumbColors() {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];

        states[0] = new int[] { android.R.attr.state_checked };
        colors[0] = binding.getTheme().accent;

        states[1] = new int[0];
        colors[1] = binding.getTheme().primary;

        return new ColorStateList(states, colors);
    }

    private ColorStateList getSwitchTrackColors() {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];

        states[0] = new int[] { android.R.attr.state_checked };

        int color = binding.getTheme().accent;
        colors[0] = Color.argb(
                (int) Math.round(Color.alpha(color)*0.25),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );

        states[1] = new int[0];
        colors[1] = binding.getTheme().onPrimaryVariant;
        return new ColorStateList(states, colors);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_backup_config, container, false);
        binding.setViewModel(new ViewModelProvider(requireActivity()).get(PreferenceViewModel.class));
        binding.setTheme(((MainActivity) requireActivity()).theme);
        View view = binding.getRoot();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"Day", "Week", "Month"});


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spSettBackupFreq.setAdapter(adapter);

        binding.btnSettBackupOptAlarm.setThumbTintList(getSwitchThumbColors());
        binding.btnSettBackupOptSleep.setThumbTintList(getSwitchThumbColors());
        binding.btnSettBackupOptDream.setThumbTintList(getSwitchThumbColors());

        binding.btnSettBackupOptAlarm.setTrackTintList(getSwitchTrackColors());
        binding.btnSettBackupOptSleep.setTrackTintList(getSwitchTrackColors());
        binding.btnSettBackupOptDream.setTrackTintList(getSwitchTrackColors());

        return view;
    }
}
