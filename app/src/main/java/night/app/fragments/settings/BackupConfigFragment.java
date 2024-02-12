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

        return binding.getRoot();
    }
}
