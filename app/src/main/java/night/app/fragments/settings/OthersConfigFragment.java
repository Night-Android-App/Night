package night.app.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.FragmentOthersConfigBinding;

public class OthersConfigFragment extends Fragment {
    FragmentOthersConfigBinding binding;

    public void requestNotificationPermission() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", requireActivity().getPackageName(), null));
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_others_config, container, false);
        binding.setTheme(((MainActivity) requireActivity()).theme);

        binding.grantNotificationPermission
                .setOnClickListener(v -> requestNotificationPermission());

        return binding.getRoot();
    }
}
