package night.app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.fragments.dialogs.LoginDialog;
import night.app.fragments.settings.BackupConfigFragment;
import night.app.fragments.settings.OthersConfigFragment;
import night.app.fragments.settings.SleepConfigFragment;
import night.app.networks.AccountRequest;
import night.app.services.DataStoreController;


public class SettingsPageFragment extends Fragment {


    private void showLoginModal() {
        new LoginDialog().show(requireActivity().getSupportFragmentManager(), "GAME_DIALOG");
    }

    @SuppressLint("CheckResult")
    private void loadAccountState(View view) {

        Preferences pref = ((MainActivity) requireActivity()).dataStore.getPreferences();

        String sessionID = pref.get(PreferencesKeys.stringKey("sessionID"));

        // sessionID isn't existed / expired in local storage
        if (sessionID == null || Boolean.FALSE.equals(AccountRequest.validateSessionID(sessionID))) {
            view.findViewById(R.id.cl_sett_acct).setOnClickListener(v -> showLoginModal());
        }
        else {
            view.findViewById(R.id.cl_sett_acct).setOnClickListener(null);

            TextView acctUID = view.findViewById(R.id.tv_sett_acct_uid);
            acctUID.setText(pref.get(PreferencesKeys.stringKey("username")));

            TextView acctDesc = view.findViewById(R.id.tv_sett_acct_desc);
            acctDesc.setText(pref.get(PreferencesKeys.stringKey("lastBackupDate")));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_page, container, false);

        loadAccountState(view);

        // init listeners inside the fragment layout
        view.findViewById(R.id.cl_sett_acct).setOnClickListener(v -> showLoginModal());

        ((TabLayout) view.findViewById(R.id.tab_sett)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Class<? extends Fragment> fragmentClass = switch (tab.getPosition()) {
                    case 0 -> SleepConfigFragment.class;
                    case 1 -> BackupConfigFragment.class;
                    case 2 -> OthersConfigFragment.class;
                    default ->
                        throw new IllegalStateException("Unexpected value: " + tab.getPosition());
                };

                requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fr_sett_details, fragmentClass, null).commit();
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
