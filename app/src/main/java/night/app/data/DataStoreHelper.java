package night.app.data;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Single;

public class DataStoreHelper {
    public final RxDataStore<Preferences> dataStore;

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

    public DataStoreHelper(RxDataStore<Preferences> ds) {
        dataStore = ds;
    }
}
