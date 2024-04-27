package night.app.fragments.widgets;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import night.app.databinding.FragmentWidgetsPageBinding;
import night.app.fragments.dialogs.AccountDialog;
import night.app.fragments.dialogs.ConfirmDialog;
import night.app.fragments.dialogs.ShopDialog;
import night.app.fragments.widgets.BackupConfigFragment;
import night.app.fragments.widgets.OthersConfigFragment;
import night.app.utils.LayoutUtils;


public class WidgetsPageFragment extends Fragment {
    private FragmentWidgetsPageBinding binding;

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
        LayoutUtils.onSelected(binding.tabSett, tab -> {
            Fragment fragment = tab.getPosition() == 0 ? new BackupConfigFragment() : new OthersConfigFragment();

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fr_sett_details, fragment)
                    .commit();
        });
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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle("Logout?")
                .setMessage("You have to login again to use part of services")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (dialog, which) -> setStylesForGuest());

        builder.show();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_widgets_page, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        setOnTabSelectedListener();

        createViewModel();
        loadDataFromDataStore();

        getChildFragmentManager()
                .setFragmentResultListener("accountStatus", this, this::setAccountStatusResult);

        binding.ibShop.setOnClickListener(v -> {
            new ShopDialog().show(requireActivity().getSupportFragmentManager(), null);
        });

        getChildFragmentManager().beginTransaction()
                .add(R.id.fr_sett_details, BackupConfigFragment.class, null)
                .commit();

        return binding.getRoot();
    }
}