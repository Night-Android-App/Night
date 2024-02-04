package night.app.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import night.app.data.AppDatabase;
import night.app.data.DataStoreHelper;
import night.app.data.PreferenceViewModel;
import night.app.data.Theme;
import night.app.databinding.ActivityMainBinding;
import night.app.fragments.AnalysisPageFragment;
import night.app.fragments.GardenPageFragment;
import night.app.fragments.SettingsPageFragment;
import night.app.R;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    public PreferenceViewModel preferenceViewModel;
    public DataStoreHelper dataStore;
    public AppDatabase appDatabase;
    public Theme theme;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> { }
            );

    private final HashMap<Integer, Class<? extends  Fragment>> pageMap = new HashMap<>() {{
        put(R.id.btn_page_garden, GardenPageFragment.class);
        put(R.id.btn_page_analysis, AnalysisPageFragment.class);
        put(R.id.btn_page_settings, SettingsPageFragment.class);
    }};


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
        Class<? extends Fragment> fragmentClass = pageMap.get(id);
        if (fragmentClass == null) return;

        // get the instance of the display fragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fr_app_page);

        for (Map.Entry<Integer, Class<? extends Fragment>> entry : pageMap.entrySet()) {
            if (entry.getValue().isInstance(fragment)) {
                setNavItemStyle(entry.getKey(), 0, theme.onPrimaryVariant);
            }
        }

        setNavItemStyle(id, LinearLayout.LayoutParams.WRAP_CONTENT, theme.onPrimary);

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fr_app_page, fragmentClass, null)
            .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataStore =  new DataStoreHelper(new RxPreferenceDataStoreBuilder(this, "settings").build());

        preferenceViewModel =  new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new PreferenceViewModel(dataStore);
            }
        }).get(PreferenceViewModel.class);

        // default (disabled): close app whatever the current fragment is
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fr_app_page);

                // exit app if fragment container contain Garden page
                if (fragment instanceof GardenPageFragment) System.exit(0);

                // if fragment container doesn't contain Garden page
                switchPage(R.id.btn_page_garden);
            }
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fr_app_page, GardenPageFragment.class, null)
                .commit();

        new Thread(() -> {
            Boolean isFirstVisited = dataStore.getPrefs().get(PreferencesKeys.booleanKey("isFirstVisited"));
            String themeID = "Default Theme";

            if (isFirstVisited == null || isFirstVisited) {
                dataStore.update(PreferencesKeys.booleanKey("isFirstVisited"), false);
                dataStore.update(PreferencesKeys.stringKey("theme"), "Default Theme");

                appDatabase = Room.databaseBuilder(this, AppDatabase.class, "app")
                        .createFromAsset("app.db")
                        .build();

                // TODO: request user accept the privacy policy

                requestPermissionLauncher.launch(new String[]{
                        "android.permission.ACTIVITY_RECOGNITION",
                        "android.permission.POST_NOTIFICATIONS"
                });
            }
            else {
                appDatabase = Room.databaseBuilder(this, AppDatabase.class, "app").build();

                String result = dataStore.getPrefs().get(PreferencesKeys.stringKey("theme"));

                if (result != null) themeID = result;
            }

            theme = appDatabase.dao().getTheme(themeID).get(0);
            binding.setTheme(theme);

            getWindow().setStatusBarColor(theme.primary);
            getWindow().setNavigationBarColor(theme.primary);
        }).start();
    }
}