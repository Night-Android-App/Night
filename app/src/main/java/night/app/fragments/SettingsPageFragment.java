package night.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.DataStoreHelper;
import night.app.data.PreferenceViewModel;
import night.app.databinding.FragmentSettingsPageBinding;
import night.app.fragments.dialogs.AccountDialog;
import night.app.fragments.dialogs.ConfirmDialog;
import night.app.fragments.settings.BackupConfigFragment;
import night.app.fragments.settings.OthersConfigFragment;


public class SettingsPageFragment extends Fragment {
    FragmentSettingsPageBinding binding;

    private void createViewModel() {
        new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new PreferenceViewModel(MainActivity.getDataStore());
            }
        }).get(PreferenceViewModel.class);
    }

    private void setOnTabSelectListener() {
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

    }

    private void setAccountStatusResult(String key, Bundle result) {
        String uid = result.getString("uid");
        String desc = result.getString("desc");

        binding.tvSettAcctUid.setText(uid);
        binding.tvSettAcctDesc.setText("You use our app since " + desc);
        binding.clSettAcct.setOnClickListener((v) -> {
            if (result.getBoolean("isOnClickLogout", false)) {
                showLogoutModal();
            }
        });

        MainActivity.getDataStore().update(DataStoreHelper.KEY_UID, uid);
        MainActivity.getDataStore().update(DataStoreHelper.KEY_ACCOUNT_CREATED, desc);
    }

    private void showLoginModal() {
        new AccountDialog().show(requireActivity().getSupportFragmentManager(), null);
    }

    private void setDefaultAccountStatus() {
        MainActivity.getDataStore().update(DataStoreHelper.KEY_UID, null);
        MainActivity.getDataStore().update(DataStoreHelper.KEY_ACCOUNT_CREATED, null);

        binding.clSettAcct.setOnClickListener(v -> showLoginModal());
        binding.tvSettAcctUid.setText("Guest");
        binding.tvSettAcctDesc.setText("Press here to login");

        MainActivity.getDataStore().update(PreferencesKeys.stringKey("sessionId"), null);
    }

    private void showLogoutModal() {
        String title = "Logout";
        String desc = "You have to login again to use part of services.";

        new ConfirmDialog(title, desc, (dialog) -> {
            setDefaultAccountStatus();
            DataStoreHelper datastore = MainActivity.getDataStore();

            datastore.update(DataStoreHelper.KEY_SESSION, null);
            datastore.update(DataStoreHelper.KEY_UID, null);
            datastore.update(DataStoreHelper.KEY_ACCOUNT_CREATED, null);

            dialog.dismiss();
        }).show(requireActivity().getSupportFragmentManager(), null);
    }


    private void initDataFromDataStore() {
        Preferences prefs = MainActivity.getDataStore().getPrefs();

        String username = prefs.get(DataStoreHelper.KEY_UID);
        String accountCreatedDate = prefs.get(DataStoreHelper.KEY_ACCOUNT_CREATED);

        if (username != null && accountCreatedDate != null) {
            binding.tvSettAcctUid.setText(username);
            binding.tvSettAcctDesc.setText("You use our app since " + accountCreatedDate);

            binding.clSettAcct.setOnClickListener(v -> showLogoutModal());
            return;
        }
        setDefaultAccountStatus();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_page, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        setOnTabSelectListener();
        createViewModel();
        initDataFromDataStore();

        getParentFragmentManager()
                .setFragmentResultListener("accountStatus", this, this::setAccountStatusResult);


        getParentFragmentManager().beginTransaction()
                .add(R.id.fr_sett_details, BackupConfigFragment.class, null)
                .commit();

        return binding.getRoot();
    }
}