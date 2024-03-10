package night.app.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.FragmentOthersConfigBinding;
import night.app.fragments.dialogs.AgreementDialog;

public class OthersConfigFragment extends Fragment {
    FragmentOthersConfigBinding binding;

    public void requestPermission() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", requireActivity().getPackageName(), null));
        startActivity(intent);
    }

    private void showAgreements(int type) {
        AgreementDialog dialog = new AgreementDialog();

        Bundle bundle = new Bundle();
        bundle.putInt("type", type);

        dialog.setArguments(bundle);
        dialog.show(getParentFragmentManager(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_others_config, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        binding.btnPermission.setOnClickListener(v -> requestPermission());

        binding.btnOpenTerm
                .setOnClickListener(v -> showAgreements(AgreementDialog.TYPE_TERMS));

        binding.btnOpenPolicy
                .setOnClickListener(v -> showAgreements(AgreementDialog.TYPE_PRIVACY));

        return binding.getRoot();
    }
}
