package night.app.fragments.settings;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import night.app.R;
import night.app.activities.AgreementActivity;
import night.app.activities.MainActivity;
import night.app.databinding.FragmentOthersConfigBinding;

public class OthersConfigFragment extends Fragment {
    FragmentOthersConfigBinding binding;

    public void requestPermission() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", requireActivity().getPackageName(), null));
        startActivity(intent);
    }

    private void showAgreements(int type) {
        Intent intent = new Intent(requireActivity(), AgreementActivity.class);

        intent.putExtra("type", type);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_others_config, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        binding.btnPermission.setOnClickListener(v -> requestPermission());

        binding.btnOpenTerm
                .setOnClickListener(v -> showAgreements(AgreementActivity.TYPE_TERMS));

        binding.btnOpenPolicy
                .setOnClickListener(v -> showAgreements(AgreementActivity.TYPE_POLICY));

        binding.btnPermission.setCompoundDrawableTintList(
                ColorStateList.valueOf(MainActivity.getAppliedTheme().getOnSurface())
        );

        binding.btnOpenTerm.setCompoundDrawableTintList(
                ColorStateList.valueOf(MainActivity.getAppliedTheme().getOnSurface())
        );

        binding.btnOpenPolicy.setCompoundDrawableTintList(
                ColorStateList.valueOf(MainActivity.getAppliedTheme().getOnSurface())
        );

        return binding.getRoot();
    }
}
