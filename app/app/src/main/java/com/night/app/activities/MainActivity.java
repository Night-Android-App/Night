package com.night.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;

import com.night.app.R;
import com.night.app.fragments.GardenPage;
import com.night.app.fragments.SettingPage;

public class MainActivity extends AppCompatActivity {
    public RxDataStore<Preferences> dataStore =
        new RxPreferenceDataStoreBuilder(this, "settings").build();


    public void switchPage(View view) {
        Class<? extends Fragment> fragmentClass;

        if (view.getId() == R.id.garden) {
            fragmentClass = GardenPage.class;
        }
        else if (view.getId() == R.id.settings) {
            fragmentClass = SettingPage.class;
        }
        else return;

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.pageFragment, fragmentClass, null).commit();
    }

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.pageFragment, GardenPage.class, null)
                .commit();
    }
}