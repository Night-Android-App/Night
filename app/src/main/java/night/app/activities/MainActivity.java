package night.app.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.graphics.Color;
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
import night.app.fragments.dialogs.PrivacyPolicyDialog;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;

    public AppDatabase appDatabase;
    public DataStoreHelper dataStore = new DataStoreHelper(this);
    public Theme theme = new Theme();

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

        setNavItemStyle(id, LinearLayout.LayoutParams.WRAP_CONTENT, theme.onPrimary);

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.setTheme(new Theme());
        getWindow().setStatusBarColor(theme.primary);
        getWindow().setNavigationBarColor(theme.primary);

        setOnBackPressedListener();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fr_app_page, GardenPageFragment.class, null)
                .commit();

        new Thread(() -> {
            appDatabase = Room.databaseBuilder(this, AppDatabase.class, "app")
                    .createFromAsset("app.db")
                    .build();

            if (dataStore.getPrefs().get(PreferencesKeys.stringKey("PolicyAgreedDate")) == null) {
                new PrivacyPolicyDialog().show(getSupportFragmentManager(), null);
                requestPermissions();
            }
            String appliedTheme = dataStore.getPrefs().get(PreferencesKeys.stringKey("theme"));
            if (theme != null) {
                List<Theme> themeList = appDatabase.dao().getTheme(appliedTheme);

                if (themeList.size() > 0) {
                    theme = themeList.get(0);
                    binding.setTheme(theme);

                    Fragment fr = getSupportFragmentManager().findFragmentById(R.id.fr_app_page);
                    if (fr instanceof GardenPageFragment) {
                        ((GardenPageFragment) fr).binding.setTheme(theme);
                    }

                    getWindow().setStatusBarColor(theme.primary);
                    getWindow().setNavigationBarColor(theme.primary);
                }
            }
        }).start();
    }
}