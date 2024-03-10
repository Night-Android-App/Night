package night.app.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import night.app.data.AppDatabase;
import night.app.data.DataStoreHelper;
import night.app.data.Theme;
import night.app.databinding.ActivityMainBinding;
import night.app.fragments.AnalysisPageFragment;
import night.app.fragments.GardenPageFragment;
import night.app.fragments.SettingsPageFragment;
import night.app.R;
import night.app.fragments.dialogs.AgreementDialog;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;

    private static AppDatabase database;

    private static DataStoreHelper dataStore;
    private static Theme theme = new Theme();

    public static void setTheme(Theme theme) {
        MainActivity.theme = theme;
    }
    public static Theme getAppliedTheme() { return MainActivity.theme; }

    private static void setDatabase(AppDatabase database) {
        MainActivity.database = database;
    }
    public static AppDatabase getDatabase() { return MainActivity.database; }

    private static void setDataStore(DataStoreHelper dataStore) {
        MainActivity.dataStore = dataStore;
    }
    public static DataStoreHelper getDataStore() {
        return MainActivity.dataStore;
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> { }
            );

    public void requestPermissions() {
        requestPermissionLauncher.launch(new String[]{
                "android.permission.ACTIVITY_RECOGNITION",
                "android.permission.POST_NOTIFICATIONS"
        });
    }

    private void setNavItemStyle(int id, int height, int color) {
        LinearLayout navItem = findViewById(id);

        // set image color
        ImageView imageView = (ImageView) navItem.getChildAt(0);
        imageView.setColorFilter(color);

        // set text visibility by layout height
        TextView textView = (TextView) navItem.getChildAt(1);

        ViewGroup.LayoutParams params = textView.getLayoutParams();
        params.height = height;

        textView.setLayoutParams(params);
    }

    // applied for event handler, which can only pass View as argument
    public void switchPage(View view) {
        switchPage(view.getId());
    }

    public void switchPage(int id) {
        HashMap<Integer, Class<? extends  Fragment>> pageMap = new HashMap<>() {{
            put(R.id.btn_page_garden, GardenPageFragment.class);
            put(R.id.btn_page_analysis, AnalysisPageFragment.class);
            put(R.id.btn_page_settings, SettingsPageFragment.class);
        }};

        Class<? extends Fragment> fragmentClass = pageMap.get(id);
        if (fragmentClass == null) return;

        // get the instance of the display fragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fr_app_page);

        for (Map.Entry<Integer, Class<? extends Fragment>> entry : pageMap.entrySet()) {
            if (entry.getValue().isInstance(fragment)) {
                setNavItemStyle(entry.getKey(), 0, theme.getOnPrimaryVariant());
            }
        }

        setNavItemStyle(id, LinearLayout.LayoutParams.WRAP_CONTENT, theme.getOnPrimary());

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fr_app_page, fragmentClass, null)
            .commit();
    }

    private void setOnBackPressedListener() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fr_app_page);

                if (fragment instanceof GardenPageFragment) System.exit(0);
                switchPage(R.id.btn_page_garden);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataStore.dispose();

        if (getSupportFragmentManager().findFragmentByTag("agreementDialog") != null) {
            ((AgreementDialog) getSupportFragmentManager().findFragmentByTag("agreementDialog")).dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataStore(new DataStoreHelper(this));

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setTheme(new Theme());
        getWindow().setStatusBarColor(theme.getPrimary());
        getWindow().setNavigationBarColor(theme.getPrimary());

        setOnBackPressedListener();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fr_app_page, GardenPageFragment.class, null)
                .commit();

        new Thread(() -> {
            setDatabase(
                    Room.databaseBuilder(this, AppDatabase.class, "app")
                            .createFromAsset("app.db")
                            .build()
            );

            Boolean isServiceStarted = dataStore.getPrefs().get(PreferencesKeys.booleanKey("isServiceStarted"));
            if (isServiceStarted == null || !isServiceStarted) {
                if (getSupportFragmentManager().findFragmentByTag("agreementDialog") == null) {
                    dataStore.update(PreferencesKeys.booleanKey("backupAlarmList"), true);
                    dataStore.update(PreferencesKeys.booleanKey("backupSleepRecord"), true);

                    Intent intent = new Intent(this, InitialActivity.class);
                    startActivity(intent);
                }
            }

            String appliedTheme = dataStore.getPrefs().get(PreferencesKeys.stringKey("theme"));
            if (theme != null) {
                List<Theme> themeList = database.dao().getTheme(appliedTheme);

                if (themeList.size() > 0) {
                    runOnUiThread(() -> {
                        MainActivity.setTheme(themeList.get(0));
                        binding.setTheme(MainActivity.getAppliedTheme());

                        Fragment fr = getSupportFragmentManager().findFragmentById(R.id.fr_app_page);
                        if (fr instanceof GardenPageFragment) {
                            ((GardenPageFragment) fr).binding.setTheme(theme);
                        }

                        getWindow().setStatusBarColor(theme.getPrimary());
                        getWindow().setNavigationBarColor(theme.getPrimary());
                    });
                }
            }
        }).start();
    }
}