package night.app.adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.DataStoreHelper;
import night.app.data.Product;
import night.app.data.Theme;
import night.app.databinding.ItemShopThemeBinding;
import night.app.fragments.GardenPageFragment;
import night.app.fragments.dialogs.PurchaseDialog;

public class ThemeViewHolder extends RecyclerView.ViewHolder {
    private final ItemShopThemeBinding binding;
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
            int preAppliedTheme = MainActivity.getAppliedTheme().prodId;

            new Thread(() -> {
                MainActivity.setTheme(theme);
                activity.binding.setTheme(MainActivity.getAppliedTheme());

                MainActivity.getDataStore().update(DataStoreHelper.KEY_THEME, theme.prodId);

                activity.runOnUiThread(() -> {
                    // update items directly, rather than updating them from the adapter
                    for (ThemeViewHolder holder : adapter.viewHolders) {
                        holder.binding.setTheme(MainActivity.getAppliedTheme());

                        if (preAppliedTheme == theme.prodId) {
                            holder.setThemePurchased();
                            continue;
                        }
                        holder.loadData();
                    }

                    // notify the shop page switch the theme (because cannot access its binding)
                    activity.getSupportFragmentManager()
                            .setFragmentResult("switchTheme", new Bundle());

                    activity.getWindow().setStatusBarColor(theme.getPrimary());
                    activity.getWindow().setNavigationBarColor(theme.getPrimary());

                    // refresh the garden page for switching theme
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fr_app_page, GardenPageFragment.class, null)
                            .commit();

                    setThemeApplied();
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
            // shop page is opened from Garden (the first navbar item), it uses active color
            if (nth == 0) {
                navItemIcon.setColorFilter(theme.getOnPrimary());
                continue;
            }
            navItemIcon.setColorFilter(theme.getOnPrimaryVariant());
        }
    }

    public void loadData() {
        if (product != null) loadData(product);
    }

    public void loadData(Product itemData) {
        product = itemData;
        MainActivity activity = adapter.activity;

        binding.setPreview(MainActivity.getAppliedTheme());

        new Thread(() -> {
            List<Theme> themes = MainActivity.getDatabase().dao().getTheme(itemData.prodId);
            theme = themes.size() > 0 ? themes.get(0) : new Theme();

            activity.runOnUiThread(() -> {
                binding.setPreview(theme);

                if (MainActivity.getAppliedTheme().prodId == theme.prodId) {
                    setThemeApplied();
                    return;
                }

                if (itemData.isBought == 1) setThemePurchased();
            });
        }).start();

        binding.tvShopItemPrice.setText(itemData.price == 0 ? "Free" : itemData.price + " coins");

        binding.btnShopItemPurchase.setOnClickListener(v -> {
            showPurchaseDialog(itemData.prodId, theme.name, itemData.price);
        });

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
    public ThemeViewHolder(ThemeAdapter adapter, ItemShopThemeBinding binding) {
        super(binding.getRoot());

        this.adapter = adapter;
        this.binding = binding;

        // disable TabLayout of the preview component
        for (View v : binding.tabAnal.getTouchables()) {
            v.setClickable(false);
        }
    }
}
