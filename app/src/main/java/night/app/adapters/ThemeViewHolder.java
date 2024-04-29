package night.app.adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.DataStoreHelper;
import night.app.data.entities.Product;
import night.app.data.entities.Theme;
import night.app.databinding.HolderThemeViewBinding;
import night.app.fragments.widgets.WidgetsPageFragment;
import night.app.fragments.dialogs.PurchaseDialog;
import night.app.utils.LayoutUtils;

public class ThemeViewHolder extends RecyclerView.ViewHolder {
    private final HolderThemeViewBinding binding;
    private final ThemeAdapter adapter;
    private Theme theme;
    private Product product;

    public void setThemeApplied() {
        binding.btnShopItemPurchase.setEnabled(false);
        binding.btnShopItemPurchase.setText("APPLIED");
        binding.tvShopItemPrice.setVisibility(View.GONE);
    }

    public void setThemePurchased() {
        binding.btnShopItemPurchase.setEnabled(true);
        binding.btnShopItemPurchase.setText("APPLY");
        binding.tvShopItemPrice.setVisibility(View.GONE);

        binding.btnShopItemPurchase.setOnClickListener(v -> {
            MainActivity activity = adapter.activity;

            new Thread(() -> {
                MainActivity.getDataStore().update(DataStoreHelper.KEY_THEME, theme.prodId);

                new Handler(Looper.getMainLooper()).post(() -> {
                    MainActivity.setTheme(theme);
                    activity.binding.setTheme(theme);

                    adapter.notifyDataSetChanged();

                    // notify the shop page switch the theme (because cannot access its binding)
                    activity.getSupportFragmentManager().setFragmentResult("switchTheme", new Bundle());

                    LayoutUtils.setSystemBarColor(adapter.activity.getWindow(), theme.getPrimary(), theme.getPrimary());

                    // refresh the garden page for switching theme
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fr_app_page, WidgetsPageFragment.class, null)
                            .commit();

                    setThemePurchased();
                    loadThemeForNavBar();
                });
            }).start();
        });
    }

    private void loadThemeForNavBar() {
        // navbar item color is updated with code rather than be updated by databind
        LinearLayout navbar = adapter.activity.binding.llNavbar;
        for (int nth=0; nth < navbar.getChildCount(); nth++) {
            LinearLayout navItemComponent = (LinearLayout) navbar.getChildAt(nth);

            ImageView navItemIcon = (ImageView) navItemComponent.getChildAt(0);
            navItemIcon.setColorFilter(nth == 2 ? theme.getOnPrimary() : theme.getOnPrimaryVariant());
        }
    }

    public void loadData() {
        if (product != null) loadData(product);
    }

    public void loadData(Product itemData) {
        product = itemData;

        binding.setTheme(MainActivity.getAppliedTheme());
        binding.setPreview(MainActivity.getAppliedTheme());

        new Thread(() -> {
            theme = MainActivity.getDatabase().dao().getTheme(itemData.prodId);
            if (theme == null) theme = new Theme();

            new Handler(Looper.getMainLooper()).post(() -> {
                binding.setPreview(theme);

                if (itemData.prodId == MainActivity.getAppliedTheme().prodId) {
                    setThemeApplied();
                    return;
                }

                if (itemData.isBought == 1 ) {
                    setThemePurchased();
                    return;
                }

                binding.btnShopItemPurchase.setText("Purchase");
                binding.btnShopItemPurchase.setEnabled(true);
                binding.btnShopItemPurchase.setOnClickListener(v -> {
                    showPurchaseDialog(itemData.prodId, theme.name, itemData.price);
                });

                binding.tvShopItemPrice.setText(itemData.price == 0 ? "Free" : itemData.price + " coins");
                binding.tvShopItemPrice.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    private void showPurchaseDialog(int prodId, String name, int price) {
        PurchaseDialog<ThemeViewHolder> dialog = new PurchaseDialog<>(this);

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("price", String.valueOf(price));
        bundle.putInt("prodId", prodId);

        dialog.setArguments(bundle);
        dialog.show(adapter.activity.getSupportFragmentManager(), null);
    }

    @SuppressLint("ClickableViewAccessibility")
    public ThemeViewHolder(ThemeAdapter adapter, HolderThemeViewBinding binding) {
        super(binding.getRoot());

        this.adapter = adapter;
        this.binding = binding;

        // disable TabLayout of the preview component
        for (View v : binding.tabAnal.getTouchables()) v.setClickable(false);
    }
}
