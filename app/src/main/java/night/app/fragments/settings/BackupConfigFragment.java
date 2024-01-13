package night.app.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import night.app.R;
import night.app.data.PreferenceViewModel;
import night.app.databinding.FragmentBackupConfigBinding;

public class BackupConfigFragment extends Fragment {
    FragmentBackupConfigBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_backup_config, container, false);
        binding.setViewModel(new ViewModelProvider(requireActivity()).get(PreferenceViewModel.class));

        View view = binding.getRoot();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"Day", "Week", "Month"});

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spSettBackupFreq.setAdapter(adapter);

        return view;
    }
}
