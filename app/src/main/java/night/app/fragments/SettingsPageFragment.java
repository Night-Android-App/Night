package night.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.Preferences;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.DataStoreHelper;
import night.app.data.PreferenceViewModel;
import night.app.databinding.FragmentSettingsPageBinding;
import night.app.fragments.dialogs.AccountDialog;
import night.app.fragments.dialogs.ConfirmDialog;
import night.app.fragments.settings.BackupConfigFragment;
import night.app.fragments.settings.OthersConfigFragment;
import night.app.utils.LayoutUtils;


public class SettingsPageFragment extends Fragment {
    private FragmentSettingsPageBinding binding;


    private void clearLocalAccountRecord() {
        DataStoreHelper dataStore = MainActivity.getDataStore();

        dataStore.update(DataStoreHelper.KEY_SESSION, null);
        dataStore.update(DataStoreHelper.KEY_UID, null);
        dataStore.update(DataStoreHelper.KEY_ACCOUNT_CREATED, null);
    }

    private void createViewModel() {
        new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new PreferenceViewModel(MainActivity.getDataStore());
            }
        }).get(PreferenceViewModel.class);
    }

    private void setOnTabSelectedListener() {
        binding.tabSett.addOnTabSelectedListener(LayoutUtils.getOnTabSelectedListener(tab -> {
            Class<? extends Fragment> fragmentClass = BackupConfigFragment.class;

            if (tab.getPosition() == 1) {
                fragmentClass = OthersConfigFragment.class;
            }

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fr_sett_details, fragmentClass, null)
                    .commit();
        }));
    }

    private void setAccountStatusResult(String key, Bundle result) {
        String uid = result.getString("uid");
        String desc = result.getString("desc");

        setStylesForUser(uid, desc);
        MainActivity.getDataStore().update(DataStoreHelper.KEY_UID, uid);
        MainActivity.getDataStore().update(DataStoreHelper.KEY_ACCOUNT_CREATED, desc);
    }

    private void showLoginModal() {
        new AccountDialog().show(requireActivity().getSupportFragmentManager(), null);
    }

    private void setStylesForUser(String uid, String accountCreatedDate) {
        binding.tvSettAcctUid.setText(uid);
        binding.tvSettAcctDesc.setText("You use our app since " + accountCreatedDate);
        binding.clSettAcct.setOnClickListener(v -> showLogoutModal());
    }

    private void setStylesForGuest() {
        clearLocalAccountRecord();

        binding.clSettAcct.setOnClickListener(v -> showLoginModal());
        binding.tvSettAcctUid.setText("Guest");
        binding.tvSettAcctDesc.setText("Press here to login");
    }

    private void showLogoutModal() {
        String title = "Logout";
        String desc = "You have to login again to use part of services.";

        new ConfirmDialog(title, desc, (dialog) -> {
            setStylesForGuest();
            dialog.dismiss();
        }).show(requireActivity().getSupportFragmentManager(), null);
    }


    private void loadDataFromDataStore() {
        Preferences prefs = MainActivity.getDataStore().getPrefs();

        String uid = prefs.get(DataStoreHelper.KEY_UID);
        String accountCreatedDate = prefs.get(DataStoreHelper.KEY_ACCOUNT_CREATED);

        if (uid != null && accountCreatedDate != null) {
            setStylesForUser(uid, accountCreatedDate);
            return;
        }
        setStylesForGuest();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_page, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        setOnTabSelectedListener();

        createViewModel();
        loadDataFromDataStore();

        getChildFragmentManager()
                .setFragmentResultListener("accountStatus", this, this::setAccountStatusResult);

        getChildFragmentManager().beginTransaction()
                .add(R.id.fr_sett_details, BackupConfigFragment.class, null)
                .commit();

        return binding.getRoot();
    }
}