package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.adapters.RingtoneAdapter;
import night.app.adapters.ThemeAdapter;
import night.app.data.entities.Product;
import night.app.data.entities.Theme;
import night.app.databinding.DialogShopBinding;
import night.app.fragments.widgets.WidgetsPageFragment;
import night.app.utils.LayoutUtils;

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

    private void switchTheme() {
        Theme theme = MainActivity.getAppliedTheme();
        binding.setTheme(theme);

        binding.tabShop.setSelectedTabIndicatorColor(theme.getOnPrimary());
        binding.tabShop.setTabTextColors(theme.getOnPrimaryVariant(), theme.getOnPrimary());

        LayoutUtils.setSystemBarColor(requireDialog().getWindow(), theme.getPrimary(), theme.getPrimary());
    }

    private void loadItemList(Integer type) {
        MainActivity activity = (MainActivity) requireActivity();
        binding.rvShopItems.removeAllViewsInLayout();

        new Thread(() -> {
            Product[] productList = MainActivity.getDatabase().dao().getProducts(type);

            activity.runOnUiThread(() -> {
                if (type == 1) {
                    binding.rvShopItems.setAdapter(new ThemeAdapter(activity, productList));
                    return;
                }

                binding.rvShopItems.setAdapter(new RingtoneAdapter(activity, productList));
            });
        }).start();
    }

    private void setOnTabSelectedListener() {
        LayoutUtils.onSelected(binding.tabShop, (tab) -> {
                releaseRingtonePlayer();
                loadItemList(tab.getPosition() == 0 ? 1 : 2);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseRingtonePlayer();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_shop, container, false);

        switchTheme();
        setOnTabSelectedListener();

        binding.btnShopClose.setOnClickListener(v -> dismiss());

        binding.rvShopItems.setLayoutManager(new LinearLayoutManager(requireActivity()));
        loadItemList(1);

        getParentFragmentManager()
                .setFragmentResultListener("switchTheme", this, (key, bundle) -> switchTheme());

        WindowInsetsControllerCompat windowController =
                WindowCompat.getInsetsController(requireDialog().getWindow(), requireDialog().getWindow().getDecorView());

        windowController.setAppearanceLightNavigationBars(true);

        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
}

