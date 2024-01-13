package night.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.FragmentSettingsPageBinding;
import night.app.fragments.dialogs.LoginDialog;
import night.app.fragments.settings.BackupConfigFragment;
import night.app.fragments.settings.OthersConfigFragment;
import night.app.fragments.settings.SleepConfigFragment;
import night.app.networks.AccountRequest;


public class SettingsPageFragment extends Fragment {
    FragmentSettingsPageBinding binding;

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
        if (getView() == null) return;

        // load data from local storage first
        // to avoid no content can show when waiting the server
        Preferences pref = ((MainActivity) requireActivity()).dataStore.data().blockingFirst();

        String uidVal = pref.get(PreferencesKeys.stringKey("username"));
        String descVal = pref.get(PreferencesKeys.stringKey("lastBackupDate"));
        // user didn't login or the app has some issues on storing data
        // xml layout is default to the view of not logged in, no need to set default view at here
        if (uidVal == null || descVal == null) return;

        binding.clSettAcct.setOnClickListener(null);

        binding.tvSettAcctUid.setText(uidVal);
        binding.tvSettAcctDesc.setText(descVal);

        // if the session was expired, request the user to login
        // and change the account view to default (not logged in)
        new AccountRequest().validateSession(res -> {

            // no action if cannot connect to the server
            // remain the account view, but the user cannot uses any server services
            if (res == null) return;

            // no action if session is valid
            if (res.optInt("responseCode") == 200) return;

            // if session is invalid
            setDefaultAcctView();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_page, container, false);

        View view = binding.getRoot();

        loadAccountState();

        // init listeners inside the fragment layout
        binding.clSettAcct.setOnClickListener(v -> showLoginModal());

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

                requireActivity().getSupportFragmentManager()
                    .beginTransaction()
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