package night.app.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import night.app.fragments.AnalysisPageFragment;
import night.app.fragments.GardenPageFragment;
import night.app.fragments.SettingsPageFragment;
import night.app.R;

public class MainActivity extends AppCompatActivity {

    public RxDataStore<Preferences> dataStore =
        new RxPreferenceDataStoreBuilder(this, "settings").build();

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


    private void setNavItemStyle(int id, int height, String color) {
        LinearLayout navItem = findViewById(id);

        // set image color
        ImageView imageView = (ImageView) navItem.getChildAt(0);
        imageView.setColorFilter(Color.parseColor(color));

        // set text visibility by layout height
        TextView textView = (TextView) navItem.getChildAt(1);

        ViewGroup.LayoutParams params = textView.getLayoutParams();
        params.height = height;

        textView.setLayoutParams(params);
    }

    public void switchPage(View view) {
        Class<? extends Fragment> fragmentClass = pageMap.get(view.getId());
        if (fragmentClass == null) return;

        // get the instance of the display fragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fr_app_page);

        // no action if user click the active button
        if (fragmentClass.isInstance(fragment)) return;

        for (Map.Entry<Integer, Class<? extends Fragment>> entry : pageMap.entrySet()) {
            if (entry.getValue().isInstance(fragment)) {
                setNavItemStyle(entry.getKey(), 0, "#A2A2A2");
            }
        }

        setNavItemStyle(view.getId(), LinearLayout.LayoutParams.WRAP_CONTENT, "#FFFFFF");

        // change the display fragment
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

        // request permission
        // if user rejected, it will not request again
        requestPermissionLauncher.launch(new String[]{
            "android.permission.ACTIVITY_RECOGNITION",
            "android.permission.POST_NOTIFICATIONS"
        });

        setNavItemStyle(R.id.btn_page_clock, 0, "#A2A2A2");
        setNavItemStyle(R.id.btn_page_analysis, 0, "#A2A2A2");
        setNavItemStyle(R.id.btn_page_settings, 0, "#A2A2A2");
    }
}