package night.app.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.google.android.material.tabs.TabLayout;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.DataStoreHelper;
import night.app.databinding.FragmentSettingsPageBinding;
import night.app.fragments.dialogs.LoginDialog;
import night.app.fragments.settings.BackupConfigFragment;
import night.app.fragments.settings.OthersConfigFragment;
import night.app.fragments.settings.SleepConfigFragment;
import night.app.networks.AccountRequest;


public class SettingsPageFragment extends Fragment {
    FragmentSettingsPageBinding binding;
    DataStoreHelper dataStore;

    private void showLoginModal() {
        new LoginDialog().show(requireActivity().getSupportFragmentManager(), null);
    }

    private void setDefaultAcctView() {
        if (getView() == null) return;

        binding.tvSettAcctUid.setText(R.string.press_here_to_login_now);
        binding.tvSettAcctDesc.setText(R.string.proceed);
        binding.clSettAcct.setOnClickListener(v -> showLoginModal());
    }

    private void loadAccountState() {
        // load data from local storage first
        // to avoid no content can show when waiting the server
        Preferences pref = dataStore.getPrefs();

        String uidVal = pref.get(PreferencesKeys.stringKey("username"));
        String descVal = pref.get(PreferencesKeys.stringKey("account_createdDate"));

        // user didn't login or the app has some issues on storing data
        // xml layout is default to the view of not logged in, no need to set default view at here
        if (uidVal == null || descVal == null) return;

        binding.clSettAcct.setOnClickListener(null);

        binding.tvSettAcctUid.setText(uidVal);
        binding.tvSettAcctDesc.setText(descVal);

        // if the session was expired, request the user to login
        // and change the account view to default (not logged in)
//        new AccountRequest().validateSession(res -> {
//
//            // no action if cannot connect to the server
//            // remain the account view, but the user cannot uses any server services
//            if (res == null) return;
//
//            // no action if session is valid
//            if (res.optInt("responseCode") == 200) return;
//
//            // if session is invalid
//            setDefaultAcctView();
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentFragmentManager().clearFragmentResultListener("accountStatus");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_page, container, false);

        View view = binding.getRoot();
        dataStore = ((MainActivity) requireActivity()).dataStore;

        binding.clSettAcct.setOnClickListener(v -> showLoginModal());

        loadAccountState();

        getParentFragmentManager().setFragmentResultListener("accountStatus", this, (reqKey, result) -> {
            String uid = result.getString("uid");
            String desc = result.getString("desc");

            binding.tvSettAcctUid.setText(uid);
            binding.tvSettAcctDesc.setText(desc);
            binding.clSettAcct.setOnClickListener(null);

            dataStore.update(PreferencesKeys.stringKey("username"), uid);
            dataStore.update(PreferencesKeys.stringKey("account_createdDate"), desc);
        });

        binding.tabSett.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Class<? extends Fragment> fragmentClass = switch (tab.getPosition()) {
                    case 0 -> SleepConfigFragment.class;
                    case 1 -> BackupConfigFragment.class;
                    case 2 -> OthersConfigFragment.class;
                    default ->
                        throw new IllegalStateException("Unexpected value: " + tab.getPosition());
                };

                requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fr_sett_details, fragmentClass, null)
                    .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        requireActivity().getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fr_sett_details, SleepConfigFragment.class, null)
                .commit();

        return view;
    }
}