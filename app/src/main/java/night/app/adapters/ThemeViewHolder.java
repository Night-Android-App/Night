package night.app.adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import night.app.activities.MainActivity;
import night.app.data.Product;
import night.app.databinding.ItemShopThemeBinding;
import night.app.fragments.dialogs.PurchaseDialog;

public class ThemeViewHolder extends RecyclerView.ViewHolder {
    ItemShopThemeBinding binding;
    ThemeAdapter adapter;

    public void setThemeApplied() {
        binding.btnShopItemPurchase.setEnabled(false);
        binding.btnShopItemPurchase.setText("APPLIED");
    }

    public void setThemePurchased(Product itemData) {
        binding.btnShopItemPurchase.setEnabled(true);
        binding.btnShopItemPurchase.setText("APPLY");

        binding.btnShopItemPurchase.setOnClickListener(v -> {
            MainActivity activity = adapter.activity;

            // temporarily store for referencing
            // activity theme object will be updated later
            String originalTheme = activity.theme.name;

            new Thread(() -> {
                activity.theme = activity.appDatabase.dao().getTheme(itemData.prodName).get(0);
                activity.binding.setTheme(activity.theme);

                activity.dataStore
                        .update(PreferencesKeys.stringKey("theme"), itemData.prodName);

                activity.runOnUiThread(() -> {

                    // update items directly, rather than updating them from the adapter
                    for (int i=0; i < adapter.viewHolders.size(); i++) {
                        ThemeViewHolder holder = adapter.viewHolders.get(i);
                        holder.binding.setTheme(activity.theme);

                        Product product = adapter.productList.get(i);
                        if (originalTheme.equals(product.prodName)) {
                            holder.setThemePurchased(product);
                        }
                    }

                    // notify the shop page switch the theme (because cannot access its binding)
                    activity.getSupportFragmentManager()
                            .setFragmentResult("switchTheme", new Bundle());

                    activity.getWindow().setStatusBarColor(activity.theme.primary);
                    activity.getWindow().setNavigationBarColor(activity.theme.primary);

                    setThemeApplied();
                });

                // navbar item color is updated with code rather than be updated by databind
                LinearLayout navbar = activity.binding.llNavbar;
                for (int nth=0; nth < navbar.getChildCount(); nth++) {
                    LinearLayout navItemComponent = (LinearLayout) navbar.getChildAt(nth);

                    ImageView navItemIcon = (ImageView) navItemComponent.getChildAt(0);
                    // shop page is opened from Garden (the first navbar item), it uses active color
                    if (nth == 0) {
                        navItemIcon.setColorFilter(activity.theme.onPrimary);
                        continue;
                    }
                    navItemIcon.setColorFilter(activity.theme.onPrimaryVariant);
                }
            }).start();
        });
    }

    public void loadData(Product itemData) {
        MainActivity activity = adapter.activity;
        binding.tvShopItemName.setText(itemData.prodName);

        new Thread(() -> {
            System.out.println(itemData.prodName);
            binding.setPreview(activity.appDatabase.dao().getTheme(itemData.prodName).get(0));
        }).start();

        if (Objects.equals(activity.theme.name, itemData.prodName)) {
            setThemeApplied();
            return;
        }

        if (itemData.prodIsBought == 1) {
            setThemePurchased(itemData);
            return;
        }

        binding.tvShopItemPrice.setText(itemData.prodPrice + " coins");

        binding.btnShopItemPurchase.setOnClickListener(v -> {
            PurchaseDialog dialog = new PurchaseDialog();

            Bundle bundle = new Bundle();
            bundle.putString("price", String.valueOf(itemData.prodPrice));

            dialog.setArguments(bundle);
            dialog.show(activity.getSupportFragmentManager(), null);

        });

    }

    @SuppressLint("ClickableViewAccessibility")
    public ThemeViewHolder(ThemeAdapter adapter, ItemShopThemeBinding binding) {
        super(binding.getRoot());

        this.adapter = adapter;
        this.binding = binding;

        LinearLayout layout = (LinearLayout) binding.tabAnal.getChildAt(0);
        for (int i=0; i < layout.getChildCount(); i++) {
            layout.getChildAt(i).setOnTouchListener((View view, MotionEvent motionEvent) -> true);
        }
    }
}
