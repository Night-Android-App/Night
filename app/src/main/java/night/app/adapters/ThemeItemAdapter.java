package night.app.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;
import java.util.Objects;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.Product;
import night.app.databinding.ItemShopThemeBinding;
import night.app.fragments.dialogs.PurchaseDialog;

class ViewHolder extends RecyclerView.ViewHolder {
    ItemShopThemeBinding binding;
    ThemeItemAdapter adapter;

    public void setThemeApplied() {
        binding.btnShopItemPurchase.setEnabled(false);
        binding.btnShopItemPurchase.setText("APPLIED");

        binding.tvShopPriceLabel.setVisibility(View.GONE);
    }

    public void setThemePurchased(Product itemData) {
        binding.btnShopItemPurchase.setEnabled(true);
        binding.btnShopItemPurchase.setText("Apply");

        binding.tvShopPriceLabel.setVisibility(View.GONE);

        binding.btnShopItemPurchase.setOnClickListener(v -> {
            MainActivity activity = adapter.activity;
            String originalTheme = activity.theme.name;

            new Thread(() -> {
                activity.theme = activity.appDatabase.dao().getTheme(itemData.prodName).get(0);
                activity.binding.setTheme(activity.theme);
                activity.dataStore.update(PreferencesKeys.stringKey("theme"), itemData.prodName);

                adapter.activity.runOnUiThread(() -> {
                    adapter.notifyItemChanged(getAdapterPosition());

                    for (int i=0; i < adapter.productList.size()-1; i++) {
                        if (adapter.productList.get(i).prodName.equals(originalTheme)) {
                            adapter.notifyItemChanged(i);
                            break;
                        }
                    }

                    adapter.activity.getSupportFragmentManager().setFragmentResult("switchTheme", new Bundle());
                    adapter.activity.getWindow().setStatusBarColor(activity.theme.primary);
                    adapter.activity.getWindow().setNavigationBarColor(activity.theme.primary);
                });

                for (int i=0; i < 4; i++) {
                    ImageView navItemImage =
                            (ImageView) ((LinearLayout) activity.binding.llNavbar.getChildAt(i)).getChildAt(0);

                    navItemImage.setColorFilter(i == 0 ? activity.theme.onPrimary: activity.theme.onPrimaryVariant);
                }

            }).start();
        });
    }

    public void loadData(Product itemData) {
        MainActivity activity = adapter.activity;
        binding.tvShopItemName.setText(itemData.prodName);

        new Thread(() ->
            binding.setPreview(activity.appDatabase.dao().getTheme(itemData.prodName).get(0))
        ).start();

        if (Objects.equals(activity.theme.name, itemData.prodName)) {
            setThemeApplied();
            return;
        }

        if (itemData.prodIsBought == 1) {
            setThemePurchased(itemData);
            return;
        }

        binding.tvShopItemPrice.setText(String.valueOf(itemData.prodPrice));

        binding.btnShopItemPurchase.setOnClickListener(v -> {
            PurchaseDialog dialog = new PurchaseDialog();

            Bundle bundle = new Bundle();
            bundle.putString("price", String.valueOf(itemData.prodPrice));

            dialog.setArguments(bundle);
            dialog.show(activity.getSupportFragmentManager(), null);

        });

    }

    public ViewHolder(ThemeItemAdapter adapter, ItemShopThemeBinding binding) {
        super(binding.getRoot());

        this.adapter = adapter;
        this.binding = binding;

        LinearLayout layout = (LinearLayout) binding.tabAnal.getChildAt(0);
        for (int i=0; i < layout.getChildCount(); i++) {
            layout.getChildAt(i).setOnTouchListener((View view, MotionEvent motionEvent) -> true);
        }
    }
}


public class ThemeItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<Product> productList;
    public final MainActivity activity;

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        ItemShopThemeBinding binding =
            DataBindingUtil.inflate(inflater, R.layout.item_shop_theme, viewGroup, false);

        binding.setTheme(activity.theme);

        return new ViewHolder(this, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ((ViewHolder) viewHolder).loadData(productList.get(position));
    }

    public ThemeItemAdapter(MainActivity mainActivity) {
        activity = mainActivity;

        new Thread(() -> {
            productList = activity.appDatabase.dao().getProducts("theme");
        }).start();
    }
}