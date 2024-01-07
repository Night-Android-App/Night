package night.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.networks.Request;
import night.app.services.DataStoreController;

public class BackupConfigFragment extends Fragment {
    View view;
    Preferences pref;

    private void backup(View view) {
        JSONObject data = new JSONObject();
    }

    private void initSwitchCompat(int id, String key) {
        SwitchCompat switchCompat = view.findViewById(id);

        switchCompat.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) requireActivity();

            mainActivity.dataStore.update(key, switchCompat.isChecked());
        });

        Boolean isChecked = pref.get(PreferencesKeys.booleanKey(key));
        if (isChecked != null) switchCompat.setChecked(isChecked);
    }

    private void loadSettings() {
        String lastBackup = pref.get(PreferencesKeys.stringKey("lastBackup"));
        TextView tvLastBackup = view.findViewById(R.id.tv_sett_backup_last_date);
        tvLastBackup.setText(lastBackup == null ? "No backup data" : lastBackup);

        initSwitchCompat(R.id.btn_sett_backup_opt_sleep, "backupSleepData");
        initSwitchCompat(R.id.btn_sett_backup_opt_alarm, "backupAlarmList");
        initSwitchCompat(R.id.btn_sett_backup_opt_dream, "backupDreamRecord");

//            String backupFrequency = pref.get(PreferencesKeys.stringKey("backupFrequency"));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_backup_config, container, false);

        pref = ((MainActivity) requireActivity()).dataStore.getPreferences();

        loadSettings();

        view.findViewById(R.id.btn_sett_backup_act_upload).setOnClickListener(this::backup);
        view.findViewById(R.id.btn_sett_backup_act_recovery);
        view.findViewById(R.id.btn_sett_backup_act_del);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new String[]{"Day", "Week", "Month"});

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) view.findViewById(R.id.sp_sett_backup_freq)).setAdapter(adapter);
        
        return view;
    }
}
