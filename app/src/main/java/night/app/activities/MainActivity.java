package night.app.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;

import night.app.fragments.AnalysisPageFragment;
import night.app.fragments.GardenPageFragment;
import night.app.fragments.SettingsPageFragment;

import night.app.R;

public class MainActivity extends AppCompatActivity {
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        if (result.get("android.permission.ACTIVITY_RECOGNITION") != null) {
                            System.out.println("granted");
                        } else {
                            System.out.println("didn't granted");
                        }
                    }
            );


    public RxDataStore<Preferences> dataStore =
        new RxPreferenceDataStoreBuilder(this, "settings").build();


    public void switchPage(View view) {
        Class<? extends Fragment> fragmentClass;

        if (view.getId() == R.id.btn_page_garden) {
            fragmentClass = GardenPageFragment.class;
        }
        else if (view.getId() == R.id.btn_page_settings) {
            fragmentClass = SettingsPageFragment.class;
        }
        else if (view.getId() == R.id.btn_page_analysis) {
            fragmentClass = AnalysisPageFragment.class;
        }
        else return;

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fr_app_page, fragmentClass, null).commit();
    }

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fr_app_page, GardenPageFragment.class, null)
                .commit();

        requestPermissionLauncher.launch(new String[]{
            "android.permission.ACTIVITY_RECOGNITION",
            "android.permission.POST_NOTIFICATIONS"
        });
    }
}