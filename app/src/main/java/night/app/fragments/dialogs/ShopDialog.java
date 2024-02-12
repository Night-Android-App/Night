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

import java.util.List;
import java.util.Objects;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.adapters.RingtoneAdapter;
import night.app.adapters.ThemeAdapter;
import night.app.data.Product;
import night.app.data.Theme;
import night.app.databinding.DialogShopBinding;

public class ShopDialog extends DialogFragment {
    DialogShopBinding binding;

    private void releaseRingtonePlayer() {
        if (binding.rvShopItems.getAdapter() instanceof RingtoneAdapter) {
            RingtoneAdapter adapter = (RingtoneAdapter) binding.rvShopItems.getAdapter();

            if (adapter.ringtonePlayer != null) {
                adapter.ringtonePlayer.release();
            }
        }
    }

    private void switchTheme(String reqKey, Bundle bundle) {
        switchTheme();
    }

    private void switchTheme() {
        Theme theme = ((MainActivity) requireActivity()).theme;
        binding.setTheme(theme);

        binding.tabShop.setSelectedTabIndicatorColor(theme.onPrimary);
        binding.tabShop.setTabTextColors(theme.getOnPrimaryVariant(), theme.onPrimary);

        requireDialog().getWindow().setStatusBarColor(theme.primary);
        requireDialog().getWindow().setNavigationBarColor(theme.secondary);
    }

    private void loadItemList(String type) {
        MainActivity activity = (MainActivity) requireActivity();
        binding.rvShopItems.removeAllViewsInLayout();

        new Thread(() -> {
            List<Product> productList = activity.appDatabase.dao().getProducts(type);

            activity.runOnUiThread(() -> {
                if (Objects.equals(type, "theme")) {
                    binding.rvShopItems.setAdapter(new ThemeAdapter(activity, productList));
                    return;
                }

                binding.rvShopItems.setAdapter(new RingtoneAdapter(activity, productList));
            });
        }).start();
    }

    private void initTabLayout() {
        binding.tabShop.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                releaseRingtonePlayer();

                loadItemList(tab.getPosition() == 0 ? "theme" :"ringtone");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseRingtonePlayer();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_shop, container, false);

        switchTheme();
        initTabLayout();

        binding.btnShopClose.setOnClickListener(v -> dismiss());

        binding.rvShopItems.setLayoutManager(new LinearLayoutManager(activity));
        loadItemList("theme");

        getParentFragmentManager()
                .setFragmentResultListener("switchTheme", this, this::switchTheme);


        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
}

