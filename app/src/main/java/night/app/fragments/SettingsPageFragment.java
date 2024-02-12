package night.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.PreferenceViewModel;
import night.app.databinding.FragmentSettingsPageBinding;
import night.app.fragments.dialogs.AccountDialog;
import night.app.fragments.settings.BackupConfigFragment;
import night.app.fragments.settings.OthersConfigFragment;


public class SettingsPageFragment extends Fragment {
    FragmentSettingsPageBinding binding;

    private void showLoginModal() {
        new AccountDialog().show(requireActivity().getSupportFragmentManager(), null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentFragmentManager().clearFragmentResultListener("accountStatus");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        MainActivity activity = (MainActivity) requireActivity();

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_page, container, false);
        binding.setTheme(activity.theme);


        new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new PreferenceViewModel(activity.dataStore);
            }
        }).get(PreferenceViewModel.class);


        binding.clSettAcct.setOnClickListener(v -> showLoginModal());

        getParentFragmentManager().setFragmentResultListener("accountStatus", this, (reqKey, result) -> {
            String uid = result.getString("uid");
            String desc = result.getString("desc");

            binding.tvSettAcctUid.setText(uid);
            binding.tvSettAcctDesc.setText(desc);
            binding.clSettAcct.setOnClickListener(null);

            activity.dataStore.update(PreferencesKeys.stringKey("username"), uid);
            activity.dataStore.update(PreferencesKeys.stringKey("account_createdDate"), desc);
        });

        binding.tabSett.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Class<? extends Fragment> fragmentClass = BackupConfigFragment.class;

                if (tab.getPosition() == 1) {
                    fragmentClass = OthersConfigFragment.class;
                }

                getParentFragmentManager().beginTransaction()
                    .replace(R.id.fr_sett_details, fragmentClass, null)
                    .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });


        getParentFragmentManager().beginTransaction()
                .add(R.id.fr_sett_details, BackupConfigFragment.class, null)
                .commit();

        return binding.getRoot();
    }
}