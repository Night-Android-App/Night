package night.app.utils;

import android.view.Window;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;


public class LayoutUtils {
    public interface onTabSelected {
        void run(TabLayout.Tab w);
    }

    public interface Function {
        void run();
    }

    public static void onSelected(TabLayout tl, onTabSelected fn) {
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { fn.run(tab); }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    public static void setSystemBarColor(Window window, int statusColor, int navColor) {
        window.setStatusBarColor(statusColor);
        window.setNavigationBarColor(navColor);
    }

    public static void onBackPressed(AppCompatActivity owner, OnBackPressedDispatcher dp, Function fn) {
        dp.addCallback(owner, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fn.run();
            }
        });
    }
}
