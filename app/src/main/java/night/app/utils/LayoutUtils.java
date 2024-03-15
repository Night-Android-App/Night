package night.app.utils;


import com.google.android.material.tabs.TabLayout;

public class LayoutUtils {
    public interface onTabSelected {
        void run(TabLayout.Tab w);
    }

    public static TabLayout.OnTabSelectedListener getOnTabSelectedListener(onTabSelected fn) {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { fn.run(tab); }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        };
    }
}
