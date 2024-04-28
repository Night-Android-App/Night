package night.app.fragments.widgets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.dao.DayDAO;
import night.app.data.entities.Alarm;
import night.app.data.dao.AlarmDAO;
import night.app.data.DataStoreHelper;
import night.app.data.entities.Day;
import night.app.data.PreferenceViewModel;
import night.app.databinding.FragmentBackupConfigBinding;
import night.app.fragments.dialogs.ConfirmDialog;
import night.app.networks.ServiceRequest;

public class BackupConfigFragment extends Fragment {
    private FragmentBackupConfigBinding binding;

    @WorkerThread
    private JSONArray getSleepDataInJson() {
        List<Day> days = MainActivity.getDatabase().dayDAO().getAll();

        JSONArray array = new JSONArray();
        for (Day day : days) array.put(day.toJSON());

        return array;
    }

    @WorkerThread
    private JSONArray getAlarmsInJson() {
        List<Alarm> alarms = MainActivity.getDatabase().alarmDAO().getAll();

        JSONArray array = new JSONArray();
        for (Alarm alarm : alarms) array.put(alarm.toJSON());

        return array;
    }

    private void backup(ConfirmDialog dialog) {
        dialog.binding.llConfirm.setVisibility(View.VISIBLE);
        dialog.binding.pbLoading.setVisibility(View.GONE);

        Preferences prefs = MainActivity.getDataStore().getPrefs();

        JSONObject requestBody = new JSONObject();
        String sid = prefs.get(PreferencesKeys.stringKey("sessionId"));
        String uid = prefs.get(PreferencesKeys.stringKey("username"));

        if (sid == null || uid == null) {
            dialog.replaceContent("Backup Error", "You have to login to use this service.", null);
            dialog.showMessage();
            return;
        }

        dialog.showLoading();
        new Thread(() -> {
            try {
                requestBody.put("sid", sid);
                requestBody.put("uid", uid);
                requestBody.put("sleepData", getSleepDataInJson().toString());
                requestBody.put("alarmList", getAlarmsInJson().toString());

                new ServiceRequest().backup(requestBody.toString(), res -> {
                    int status = res.optInt("responseCode");
                    if (status == 200) {
                        requireActivity().runOnUiThread(() -> {
                            dialog.replaceContent("Backup Success", "The data should be available in local", null);
                            dialog.showMessage();

                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
                            format.setTimeZone(TimeZone.getDefault());

                            binding.tvSettBackupLastDate.setText(format.format(new Date()));
                            MainActivity.getDataStore().update(
                                    PreferencesKeys.stringKey("lastBackupDate"),
                                    format.format(new Date())
                            );
                        });
                        return;
                    }

                    String errorMsg = switch (status) {
                        case 400 -> "Incorrect request structure";
                        case 401 -> "Unauthorized user";
                        case 404 -> "Request path not found";
                        case 500 -> "Internal server error. Try again later.";
                        default -> "Unexpected error status: " + status;
                    };

                    requireActivity().runOnUiThread(() -> {
                        dialog.replaceContent("Backup Error", errorMsg, null);
                        dialog.showMessage();
                    });
                });
            }
            catch (JSONException e) {
                requireActivity().runOnUiThread(() -> {
                    dialog.replaceContent("Backup Error", "Unexpected error (JSONException)", null);
                    dialog.showMessage();
                });
            }
            dialog.dismiss();
        }).start();
    }

    private void recoveryCallBack(JSONObject res) {
        try {
            // delete all data in the local
            MainActivity.getDatabase().dayDAO().deleteAll();
            MainActivity.getDatabase().sleepEventDAO().deleteAll();

            MainActivity.getDatabase().alarmDAO().discardAll();

            JSONObject responseBody = res.getJSONObject("response");

            Day.resolveJSON(new JSONArray(responseBody.getString("sleepData")));
            Alarm.resolveJSON(new JSONArray(responseBody.getString("alarmList")));
        }
        catch (JSONException e) {
            System.err.println(e);
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
        dialog.dismiss();
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
