package night.app.data;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Single;

public class DataStoreHelper {
    private final RxDataStore<Preferences> dataStore;

    public void dispose() {
        dataStore.dispose();
    }

    public Preferences getPrefs() {
        return dataStore.data().blockingFirst();
    }

    public <T> void update(Preferences.Key<T> key, T value) {
        Single<Preferences> updateResult = dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();

            mutablePreferences.set(key, value);
            return Single.just(mutablePreferences);
        });
    }

    public DataStoreHelper(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context, "settings").build();
    }
}
