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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_backup_config, container, false);

        binding.setTheme(activity.theme);
        binding.setViewModel(new ViewModelProvider(activity).get(PreferenceViewModel.class));

        String[] frequencyOptions = new String[] {"Day", "Week", "Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                activity, android.R.layout.simple_spinner_item, frequencyOptions
        );


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spSettBackupFreq.setAdapter(adapter);

        for (SwitchCompat switchCompat : new SwitchCompat[] { binding.btnSettBackupOptAlarm, binding.btnSettBackupOptSleep, binding.btnSettBackupOptDream }) {
            switchCompat.setThumbTintList(binding.getTheme().getSwitchThumbColors());
            switchCompat.setTrackTintList(binding.getTheme().getSwitchTrackColors());
        }

        return binding.getRoot();
    }
}
