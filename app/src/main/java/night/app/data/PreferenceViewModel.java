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
            lastBackupDate = prefs.get(DataStoreHelper.KEY_BACKUP_DATE);
        }
        if (lastBackupDate == null || lastBackupDate.equals("")) return "No record";
        return lastBackupDate;
    }

    public void setLastBackupDate(String value) {
        if (lastBackupDate.equals(value)) return;
        lastBackupDate = value;
        dataStore.update(DataStoreHelper.KEY_BACKUP_DATE, value);
    }

    @Bindable
    public Boolean getIsBackupSleep() {
        if (isBackupSleep == null) {
            isBackupSleep = prefs.get(DataStoreHelper.KEY_BACKUP_SLEEP);
        }
        return isBackupSleep;
    }

    public void setIsBackupSleep(Boolean value) {
        if (isBackupSleep == value) return;

        isBackupSleep = value;
        dataStore.update(DataStoreHelper.KEY_BACKUP_SLEEP, value);
    }

    @Bindable
    public Boolean getIsBackupAlarm() {
        if (isBackupAlarm == null) {
            isBackupAlarm = prefs.get(DataStoreHelper.KEY_BACKUP_ALARM);
        }
        return isBackupAlarm;
    }

    public void setIsBackupAlarm(Boolean value) {
        if (isBackupAlarm == value) return;

        isBackupAlarm = value;
        dataStore.update(DataStoreHelper.KEY_BACKUP_ALARM, value);
    }

    @Bindable
    public Boolean getIsBackupDream() {
        if (isBackupDream == null) {
            isBackupDream = prefs.get(DataStoreHelper.KEY_BACKUP_DREAM);
        }
        return isBackupDream;
    }

    public void setIsBackupDream(Boolean value) {
        if (isBackupDream == value) return;

        isBackupDream = value;
        dataStore.update(DataStoreHelper.KEY_BACKUP_DREAM, value);
    }

    public PreferenceViewModel(DataStoreHelper dataStoreHelper) {
        dataStore = dataStoreHelper;
        prefs = dataStore.getPrefs();
    }
}