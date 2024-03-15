package night.app.data;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Single;

public class DataStoreHelper {
    private final RxDataStore<Preferences> dataStore;
    public final static Preferences.Key<Integer> KEY_THEME = PreferencesKeys.intKey("theme");
    public final static Preferences.Key<Boolean>
            KEY_SERVICE_STARTED = PreferencesKeys.booleanKey("isServiceStarted");
    public final static Preferences.Key<String> KEY_UID = PreferencesKeys.stringKey("username");
    public final static Preferences.Key<String> KEY_SESSION = PreferencesKeys.stringKey("sessionId");
    public final static Preferences.Key<String>
            KEY_ACCOUNT_CREATED = PreferencesKeys.stringKey("account_createdDate");
    public final static Preferences.Key<Integer> KEY_COINS = PreferencesKeys.intKey("coins");
    public final static Preferences.Key<String>
            KEY_BACKUP_DATE = PreferencesKeys.stringKey("lastBackupDate");
    public final static Preferences.Key<Boolean>
            KEY_BACKUP_ALARM = PreferencesKeys.booleanKey("backupAlarmList");
    public final static Preferences.Key<Boolean>
            KEY_BACKUP_SLEEP = PreferencesKeys.booleanKey("backupSleepRecord");
    public final static Preferences.Key<Boolean>
            KEY_BACKUP_DREAM = PreferencesKeys.booleanKey("backupDreamRecord");
    public final static Preferences.Key<Integer>
            KEY_TOTAL_EARNED = PreferencesKeys.intKey("totalEarned");
    public final static Preferences.Key<Integer>
            KEY_PREY_CAUGHT = PreferencesKeys.intKey("preyCaught");
    public final static Preferences.Key<Integer>
            KEY_SALE_PRICE = PreferencesKeys.intKey("salePrice");

    public <T> void update(Preferences.Key<T> key, T value) {
        Single<Preferences> updateResult = dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();

            mutablePreferences.set(key, value);
            return Single.just(mutablePreferences);
        });
    }

    public Preferences getPrefs() {
        return dataStore.data().blockingFirst();
    }


    public void dispose() {
        dataStore.dispose();
    }

    public DataStoreHelper(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context, "settings").build();
    }
}
