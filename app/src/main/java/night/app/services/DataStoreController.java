package night.app.services;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.rxjava3.core.Single;
import night.app.activities.MainActivity;


public class DataStoreController {
    public RxDataStore<Preferences> dataStore;


    public Preferences getPreferences() {
        return dataStore.data().map(pref -> pref).blockingFirst();
    }

    public void update(String key, Boolean value) {
        Single<Preferences> updateResult = dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();

            mutablePreferences.set(PreferencesKeys.booleanKey(key), value);
            return Single.just(mutablePreferences);
        });
    }

    public void update(String key, String value) {
        Single<Preferences> updateResult = dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();

            mutablePreferences.set(PreferencesKeys.stringKey(key), value);
            return Single.just(mutablePreferences);
        });
    }

    public DataStoreController(RxDataStore<Preferences> dataStore) {
        this.dataStore = dataStore;
    }
}
