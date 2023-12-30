package com.night.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;

import com.night.app.R;
import com.night.app.activities.MainActivity;
import com.night.app.networks.AccountRequest;

import io.reactivex.rxjava3.disposables.Disposable;

public class SettingPage extends Fragment {
    public void requestNotificationPermission() {
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
    }

    private void showLoginModal() {
        new LoginModal().show(requireActivity().getSupportFragmentManager(), "GAME_DIALOG");
    }

    private void loadAccountState(View view) {
        TextView uidTextView = view.findViewById(R.id.uid);
        TextView backupTextView = view.findViewById(R.id.lastBackupDate);

        Disposable disposable = ((MainActivity) requireActivity()).dataStore.data().forEach(pref -> {
            String sessionID = pref.get(PreferencesKeys.stringKey("sessionID"));

            // sessionID isn't existed / expired in local storage
            if (sessionID == null || !AccountRequest.validateSessionID(sessionID)) {
                uidTextView.setText("Press here to login now");
                backupTextView.setText(">> Proceed");
                view.findViewById(R.id.account).setOnClickListener(v -> showLoginModal());
            } else {
                uidTextView.setText(pref.get(PreferencesKeys.stringKey("username")));
                backupTextView.setText(pref.get(PreferencesKeys.stringKey("lastBackupDate")));
            }
        });
    }

    public SettingPage() {
        super(R.layout.fragment_setting);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        view.findViewById(R.id.grantNotificationPermission)
            .setOnClickListener(v -> requestNotificationPermission());

        view.findViewById(R.id.account)
            .setOnClickListener(v -> showLoginModal());

        loadAccountState(view);

        return view;
    }
}
