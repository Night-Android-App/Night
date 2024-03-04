package night.app.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.Alarm;
import night.app.data.AppDAO;
import night.app.data.Day;
import night.app.data.PreferenceViewModel;
import night.app.databinding.FragmentBackupConfigBinding;
import night.app.networks.ServiceRequest;

public class BackupConfigFragment extends Fragment {
    FragmentBackupConfigBinding binding;

    private JSONObject dayToJSON(Day day) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("date", day.date);
        json.put("sleep", day.sleep);

        Boolean isBackupDream = binding.getViewModel().getIsBackupDream();
        json.put("dream", isBackupDream == null || !isBackupDream ? day.dream : null);
        return json;
    }

    private JSONObject alarmToJSON(Alarm alarm) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("id", alarm.id);
        json.put("startTime", alarm.startTime);
        json.put("endTime", alarm.endTime);
        json.put("isAlarmEnabled", alarm.isAlarmEnabled);
        json.put("isDNDEnabled", alarm.isDNDEnabled);
        return json;
    }

    private <T> JSONObject elementToJson(List<T> list) throws JSONException{
        JSONObject jsonObject = new JSONObject();
        for (int i=0; i < list.size(); i++) {
            T element = list.get(i);
            if (element instanceof Day) {
                jsonObject.put(String.valueOf(((Day) element).date), dayToJSON((Day) element));
                continue;
            }
            jsonObject.put(String.valueOf(((Alarm) element).id), alarmToJSON((Alarm) element));
        }
        return jsonObject;
    }

    private void backup() {
        MainActivity activity = (MainActivity) requireActivity();
        Preferences prefs = activity.dataStore.getPrefs();

        JSONObject requestBody = new JSONObject();

        new Thread(() -> {
            try {
                List<Day> days = activity.appDatabase.dao().getAllDay();
                List<Alarm> alarms = activity.appDatabase.dao().getAllAlarms();

                requestBody.put("sid", prefs.get(PreferencesKeys.stringKey("sessionId")));
                requestBody.put("uid", prefs.get(PreferencesKeys.stringKey("username")));

                requestBody.put("sleepData", elementToJson(days).toString());
                requestBody.put("alarmList", elementToJson(alarms).toString());

                new ServiceRequest().backup(requestBody.toString(), res -> {
                    if (res.optInt("status") == 200) {
                        System.out.println("200");
                    }
                    else {
                        System.out.println(res);
                    }
                });
            }
            catch (JSONException e) {
                System.err.println("Failed to backup. (JSONException)");
            }
        }).start();
    }

    private void recoverySleepData(String data) throws JSONException {
        AppDAO dao = ((MainActivity) requireActivity()).appDatabase.dao();

        JSONObject sleepData = new JSONObject(data);
        Iterator<String> keys = sleepData.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject day = sleepData.getJSONObject(key);

            dao.insertDay(
                    Integer.parseInt(key),
                    day.getString("sleep"),
                    day.has("dream") ? day.getString("dream") : null
            );
        }
    }

    private void recoveryAlarmList(String data) throws JSONException {
        AppDAO dao = ((MainActivity) requireActivity()).appDatabase.dao();

        JSONObject alarmList = new JSONObject(data);
        Iterator<String> keys = alarmList.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject alarm = alarmList.getJSONObject(key);

            dao.createAlarm(
                    alarm.getInt("endTime"),
                    alarm.getInt("isAlarmEnabled"),
                    alarm.getInt("isDNDEnabled"),
                    alarm.optString("ringtone", "default")
            );
        }
    }

    private void recoveryCallBack(JSONObject res) {
        try {
            AppDAO dao = ((MainActivity) requireActivity()).appDatabase.dao();
            dao.deleteAllDays();

            JSONObject responseBody = res.getJSONObject("response");

            recoverySleepData(responseBody.getString("sleepData"));
            recoveryAlarmList(responseBody.getString("alarmList"));
        }
        catch (JSONException e) {
            System.out.println(e);
        }
    }

    private void recovery() {
        try {
            MainActivity activity = (MainActivity) requireActivity();
            JSONObject requestBody = new JSONObject();

            Preferences prefs = activity.dataStore.getPrefs();
            requestBody.put("sid", prefs.get(PreferencesKeys.stringKey("sessionId")));
            requestBody.put("uid", prefs.get(PreferencesKeys.stringKey("username")));

            new ServiceRequest().recovery(requestBody.toString(), this::recoveryCallBack);
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
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
