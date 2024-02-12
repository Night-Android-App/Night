package night.app.data;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.lifecycle.ViewModel;

public class PreferenceViewModel extends ViewModel implements Observable {
    private final DataStoreHelper dataStore;

    // used for initialize data only, will not update during the process
    private final Preferences prefs;

    // Preferences
    private String lastBackupDate;
    private Boolean isBackupSleep, isBackupAlarm, isBackupDream;

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) { }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) { }

    @Bindable
    public String getLastBackupDate() {
        if (lastBackupDate == null) {
            lastBackupDate = prefs.get(PreferencesKeys.stringKey("lastBackupDate"));
        }
        if (lastBackupDate == null || lastBackupDate.equals("")) return "No backup data";
        return lastBackupDate;
    }

    public void setLastBackupDate(String value) {
        if (lastBackupDate.equals(value)) return;
        lastBackupDate = value;
        dataStore.update(PreferencesKeys.stringKey("lastBackupDate"), value);
    }

    @Bindable
    public Boolean getIsBackupSleep() {
        if (isBackupSleep == null) {
            isBackupSleep = prefs.get(PreferencesKeys.booleanKey("backupSleepRecord"));
        }
        return isBackupSleep;
    }

    public void setIsBackupSleep(Boolean value) {
        if (isBackupSleep == value) return;

        isBackupSleep = value;
        dataStore.update(PreferencesKeys.booleanKey("backupSleepRecord"), value);
    }

    @Bindable
    public Boolean getIsBackupAlarm() {
        if (isBackupAlarm == null) {
            isBackupAlarm = prefs.get(PreferencesKeys.booleanKey("backupAlarmList"));
        }
        return isBackupAlarm;
    }

    public void setIsBackupAlarm(Boolean value) {
        if (isBackupAlarm == value) return;

        isBackupAlarm = value;
        dataStore.update(PreferencesKeys.booleanKey("backupAlarmList"), value);
    }

    @Bindable
    public Boolean getIsBackupDream() {
        if (isBackupDream == null) {
            isBackupDream = prefs.get(PreferencesKeys.booleanKey("backupDreamRecord"));
        }
        return isBackupDream;
    }

    public void setIsBackupDream(Boolean value) {
        if (isBackupDream == value) return;

        isBackupDream = value;
        dataStore.update(PreferencesKeys.booleanKey("backupDreamRecord"), value);
    }

    public PreferenceViewModel(DataStoreHelper dataStoreHelper) {
        dataStore = dataStoreHelper;
        prefs = dataStore.getPrefs();
    }
}