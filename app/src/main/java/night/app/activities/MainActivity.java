package night.app.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import night.app.data.AppDatabase;
import night.app.data.DataStoreHelper;
import night.app.data.Theme;
import night.app.databinding.ActivityMainBinding;
import night.app.fragments.AnalysisPageFragment;
import night.app.fragments.ClockPageFragment;
import night.app.fragments.WidgetsPageFragment;
import night.app.R;
import night.app.fragments.clocks.AlarmFragment;
import night.app.fragments.clocks.NapFragment;
import night.app.utils.LayoutUtils;

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

    public void switchPage(int destId, Class<? extends Fragment> destFragment) {
        for (int id : new int[] {R.id.btn_page_clock, R.id.btn_page_analysis, R.id.btn_page_settings}) {
            if (id == destId) {
                setNavItemStyle(id, LinearLayout.LayoutParams.WRAP_CONTENT, theme.getOnPrimary());
                continue;
            }
            setNavItemStyle(id, 0, theme.getOnPrimaryVariant());
        }

        Fragment fr = getSupportFragmentManager().findFragmentById(R.id.fr_app_page);
        if (fr != null && fr.getClass() == destFragment) return;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fr_app_page, destFragment, null)
                .commit();
    }

    private void setOnBackPressedListener() {
        LayoutUtils.onBackPressed(this, getOnBackPressedDispatcher(), () -> {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fr_app_page);

            if (fragment instanceof ClockPageFragment) System.exit(0);
            switchPage(R.id.btn_page_clock, ClockPageFragment.class);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataStore.dispose();

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDataStore(new DataStoreHelper(this));

        if (getIntent() != null && getIntent().hasExtra("alarmId")) {
            startActivity(new Intent(this, SleepActivity.class));
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setTheme(new Theme());
        getWindow().setStatusBarColor(theme.getPrimary());
        getWindow().setNavigationBarColor(theme.getPrimary());

        binding.btnPageClock
                .setOnClickListener(v -> switchPage(v.getId(), ClockPageFragment.class));
        binding.btnPageAnalysis
                .setOnClickListener(v -> switchPage(v.getId(), AnalysisPageFragment.class));
        binding.btnPageSettings
                .setOnClickListener(v -> switchPage(v.getId(), WidgetsPageFragment.class));

        setOnBackPressedListener();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fr_app_page, ClockPageFragment.class, null)
                .commit();

        new Thread(() -> {
            setDatabase(
                    Room.databaseBuilder(this, AppDatabase.class, "app")
                            .createFromAsset("app.db")
                            .build()
            );

            Boolean isServiceStarted = dataStore.getPrefs().get(DataStoreHelper.KEY_SERVICE_STARTED);
            if (isServiceStarted == null || !isServiceStarted) {
                dataStore.update(DataStoreHelper.KEY_BACKUP_ALARM, true);
                dataStore.update(DataStoreHelper.KEY_BACKUP_SLEEP, true);
            }

            Integer appliedTheme = dataStore.getPrefs().get(DataStoreHelper.KEY_THEME);
            if (appliedTheme != null) {
                List<Theme> themeList = database.dao().getTheme(appliedTheme);

                if (themeList.size() > 0) {
                    runOnUiThread(() -> {
                        MainActivity.setTheme(themeList.get(0));
                        binding.setTheme(MainActivity.getAppliedTheme());

                        Fragment fr = getSupportFragmentManager().findFragmentById(R.id.fr_app_page);
                        if (fr instanceof ClockPageFragment) {
                            ((ClockPageFragment) fr).binding.setTheme(theme);

                            Fragment innerFr = fr.getChildFragmentManager().findFragmentById(R.id.fr_clock_details);
                            if (innerFr instanceof AlarmFragment) {
                                ((AlarmFragment) innerFr).binding.setTheme(getAppliedTheme());
                            }
                            else if (innerFr instanceof NapFragment) {
                                ((NapFragment) innerFr).binding.setTheme(theme);
                            }
                        }

                        LayoutUtils.setSystemBarColor(getWindow(), theme.getPrimary(), theme.getPrimary());
                    });
                }
            }
        }).start();

        requestPermissions();
    }
}