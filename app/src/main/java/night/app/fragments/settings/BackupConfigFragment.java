package night.app.fragments.settings;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.Alarm;
import night.app.data.Day;
import night.app.data.PreferenceViewModel;
import night.app.databinding.FragmentBackupConfigBinding;
import night.app.networks.ServiceRequest;

public class BackupConfigFragment extends Fragment {
    FragmentBackupConfigBinding binding;

    private String dayToJSON(Day day) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("date", day.date);
        json.put("sleep", day.sleep);
        json.put("dream", binding.getViewModel().getIsBackupDream() ? day.dream : null);

        return json.toString();
    }

    private String alarmToJSON(Alarm alarm) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("id", alarm.id);
        json.put("startTime", alarm.startTime);
        json.put("endTime", alarm.endTime);
        json.put("isAlarmEnabled", alarm.isAlarmEnabled);
        json.put("isDNDEnabled", alarm.isDNDEnabled);
        return json.toString();
    }

    private <T> String[] elementToJson(List<T> list) throws JSONException{
        String[] tempArray = new String[list.size()];

        for (int i=0; i < tempArray.length; i++) {
            T element = list.get(i);
            if (element instanceof Day) {
                tempArray[i] = dayToJSON((Day) element);
                continue;
            }
            tempArray[i] = alarmToJSON((Alarm) element);
        }
        return tempArray;
    }

    private void backup() {
        MainActivity activity = (MainActivity) requireActivity();
        Preferences prefs = activity.dataStore.getPrefs();

        JSONObject requestBody = new JSONObject();

        try {
            Integer lastBackupDate = prefs.get(PreferencesKeys.intKey("lastBackupDate"));
            requestBody.put("lastBackupDate", lastBackupDate);

            List<Day> days = activity.appDatabase.dao().getAllDay();
            List<Alarm> alarms = activity.appDatabase.dao().getAllAlarms();

            requestBody.put("sleep", elementToJson(days));
            requestBody.put("alarm", elementToJson(alarms));

            new ServiceRequest().backup(requestBody.toString(), (res -> {
                if (res.optInt("status") == 200) {

                }
                else {

                }
            }));
        }
        catch (JSONException e) {
            System.err.println("Failed to backup. (JSONException)");
        }
    }

    private void recovery() {
        new ServiceRequest().recovery((res) -> {

        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_backup_config, container, false);

        binding.setTheme(activity.theme);
        binding.setViewModel(new ViewModelProvider(activity).get(PreferenceViewModel.class));

        binding.btnSettBackupActUpload.setOnClickListener(v -> backup());
        binding.btnSettBackupActRecovery.setOnClickListener(v -> recovery());

        return binding.getRoot();
    }
}
