package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.adapters.ThemeItemAdapter;
import night.app.data.Theme;
import night.app.databinding.DialogShopBinding;

public class ShopDialog extends DialogFragment {
    DialogShopBinding binding;


    private void switchTheme(String reqKey, Bundle bundle) {
        switchTheme();
    }

    private void switchTheme() {
        Theme theme = ((MainActivity) requireActivity()).theme;
        binding.setTheme(theme);

        binding.tabShop.setSelectedTabIndicatorColor(theme.onPrimary);
        binding.tabShop.setTabTextColors(theme.onPrimaryVariant, theme.onPrimary);

        requireDialog().getWindow().setStatusBarColor(theme.primary);
        requireDialog().getWindow().setNavigationBarColor(theme.secondary);
    }

    private void loadItemList(RecyclerView.Adapter adapter) {
        RecyclerView itemListView = binding.rvShopItems;
        itemListView.removeAllViewsInLayout();

        itemListView.setAdapter(adapter);
    }

    private void initTabLayout() {
        MainActivity activity = (MainActivity) requireActivity();

        binding.tabShop.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0 -> loadItemList(new ThemeItemAdapter(activity));

                    case 1 -> binding.rvShopItems.removeAllViewsInLayout();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_shop, container, false);

        switchTheme();

        MainActivity activity = (MainActivity) requireActivity();
        binding.btnShopClose.setOnClickListener(v -> dismiss());

        initTabLayout();

        binding.rvShopItems.setLayoutManager(new LinearLayoutManager(activity));

        new Thread(() -> loadItemList(new ThemeItemAdapter(activity))).start();

        getParentFragmentManager()
                .setFragmentResultListener("switchTheme", this, this::switchTheme);

        return binding.getRoot();
    }
}

