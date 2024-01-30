package night.app.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.HashMap;

import night.app.R;
import night.app.databinding.ItemShopThemeBinding;
import night.app.fragments.dialogs.PurchaseDialog;

class ViewHolder extends RecyclerView.ViewHolder {
    ItemShopThemeBinding binding;

    public ViewHolder(ItemShopThemeBinding binding) {
        super(binding.getRoot());

        this.binding = binding;
    }

    public void loadData(FragmentActivity activity, HashMap<String, String> itemData) {
        binding.tvShopItemName.setText(itemData.getOrDefault("name", ""));
        binding.tvShopItemPrice.setText(itemData.getOrDefault("price", ""));

        binding.btnShopItemPurchase.setOnClickListener(v -> {
            PurchaseDialog dialog = new PurchaseDialog();


            Bundle bundle = new Bundle();

            bundle.putString("price", itemData.getOrDefault("price", ""));
            dialog.setArguments(bundle);

            dialog.show(activity.getSupportFragmentManager(), null);

        });
    }
}

public class ThemeItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<HashMap<String, String>> localDataSet;
    private final FragmentActivity activity;

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    @Override @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        ItemShopThemeBinding binding =
            DataBindingUtil.inflate(inflater, R.layout.item_shop_theme, viewGroup, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ((ViewHolder) viewHolder).loadData(activity, localDataSet.get(position));
    }

    public ThemeItemAdapter(FragmentActivity mainActivity, ArrayList<HashMap<String, String>> dataSet) {
        activity = mainActivity;
        localDataSet = dataSet;
    }
}