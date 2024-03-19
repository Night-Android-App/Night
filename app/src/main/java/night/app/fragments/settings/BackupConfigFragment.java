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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.Alarm;
import night.app.data.AppDAO;
import night.app.data.DataStoreHelper;
import night.app.data.Day;
import night.app.data.PreferenceViewModel;
import night.app.databinding.FragmentBackupConfigBinding;
import night.app.fragments.dialogs.ConfirmDialog;
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

    private void backup(ConfirmDialog dialog) {
        dialog.binding.llConfirm.setVisibility(View.VISIBLE);
        dialog.binding.pbLoading.setVisibility(View.GONE);

        MainActivity activity = (MainActivity) requireActivity();
        Preferences prefs = MainActivity.getDataStore().getPrefs();

        JSONObject requestBody = new JSONObject();
        new Thread(() -> {
            List<Day> days = MainActivity.getDatabase().dao().getAllDay();
            List<Alarm> alarms = MainActivity.getDatabase().dao().getAllAlarms();

            activity.runOnUiThread(() -> {
                try {
                    String sid = prefs.get(PreferencesKeys.stringKey("sessionId"));
                    String uid = prefs.get(PreferencesKeys.stringKey("username"));

                    if (sid == null || uid == null) {
                        dialog.replaceContent("Backup Error", "You have to login to use this service.", null);
                        dialog.showMessage();
                        return;
                    }
                    requestBody.put("sid", sid);
                    requestBody.put("uid", uid);

                    requestBody.put("sleepData", elementToJson(days).toString());
                    requestBody.put("alarmList", elementToJson(alarms).toString());

                    dialog.showLoading();
                    new ServiceRequest().backup(requestBody.toString(), res -> {

                        int status = res.optInt("responseCode");
                        if (status == 200) {
                            activity.runOnUiThread(() -> {
                                dialog.replaceContent("Backup Success", "The data should be available in local", null);
                                dialog.showMessage();

                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                df.setTimeZone(TimeZone.getDefault());

                                binding.tvSettBackupLastDate.setText(df.format(new Date()));
                                MainActivity.getDataStore().update(
                                    PreferencesKeys.stringKey("lastBackupDate"),
                                    df.format(new Date())
                                );
                            });
                            return;
                        }

                        String errorMsg = switch (status) {
                            case 400 -> "Incorrect request structure";
                            case 401 -> "Unauthorized user";
                            case 404 -> "Request path not found";
                            case 500 -> "Internal server error. Try again later.";
                            default  -> "Unexpected error status: " + status;
                        };

                        dialog.replaceContent("Backup Error", errorMsg, null);
                        dialog.showMessage();
                    });
                }
                catch (JSONException e) {
                    dialog.replaceContent("Backup Error", "Unexpected error (JSONException)", null);
                    dialog.showMessage();
                }
            });
        }).start();
    }

    private void recoverySleepData(String data) throws JSONException {
        AppDAO dao = MainActivity.getDatabase().dao();

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
        AppDAO dao = MainActivity.getDatabase().dao();

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
            AppDAO dao = MainActivity.getDatabase().dao();
            dao.deleteAllDays();
            dao.deleteAllAlarms();

            JSONObject responseBody = res.getJSONObject("response");

            recoverySleepData(responseBody.getString("sleepData"));
            recoveryAlarmList(responseBody.getString("alarmList"));
        }
        catch (JSONException e) {
            System.out.println(e);
        }
    }

    private void recovery(ConfirmDialog dialog) {
        try {
            MainActivity activity = (MainActivity) requireActivity();
            JSONObject requestBody = new JSONObject();

            Preferences prefs = MainActivity.getDataStore().getPrefs();

            String sid = prefs.get(DataStoreHelper.KEY_SESSION);
            String uid = prefs.get(DataStoreHelper.KEY_UID);

            if (sid == null || uid == null) {
                dialog.replaceContent("Recovery Error", "You have to login to use this service.", null);
                return;
            }

            dialog.showLoading();

            requestBody.put("sid", sid);
            requestBody.put("uid", uid);

            new ServiceRequest().recovery(requestBody.toString(), (res) -> {

                int status = res.optInt("responseCode");
                if (status == 200) {
                    recoveryCallBack(res);
                    new ConfirmDialog("Backup Success", "The data should be available in local", null)
                            .show(activity.getSupportFragmentManager(), null);
                    return;
                }

                String errorMsg = switch (status) {
                    case 400 -> "Incorrect request structure";
                    case 401 -> "Unauthorized user";
                    case 404 -> "Request path not found";
                    case 500 -> "Internal server error. Try again later.";
                    default  -> "Unexpected error status: " + status;
                };

                new ConfirmDialog("Backup Error", errorMsg, null)
                        .show(getParentFragmentManager(), null);
            });
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_backup_config, container, false);

        binding.setTheme(MainActivity.getAppliedTheme());
        binding.setViewModel(new ViewModelProvider(activity).get(PreferenceViewModel.class));

        binding.tvSettBackupLastDate.setText(
                MainActivity.getDataStore().getPrefs().get(DataStoreHelper.KEY_BACKUP_DATE)
        );

        binding.btnSettBackupActUpload.setOnClickListener(v -> {
            String title = "Backup";
            String desc = "This action will replace the previous backup and unable to recovery.";
            new ConfirmDialog(title, desc, this::backup).show(getParentFragmentManager(), null);
        });

        binding.btnSettBackupActRecovery.setOnClickListener(v -> {
            String title = "Recovery";
            String desc = "This action will replace the local record and unable to recovery.";
            new ConfirmDialog(title, desc, this::recovery).show(getParentFragmentManager(), null);
        });

        return binding.getRoot();
    }
}
