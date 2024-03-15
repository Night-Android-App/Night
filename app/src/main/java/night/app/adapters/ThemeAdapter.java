package night.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.Product;
import night.app.databinding.ItemShopThemeBinding;

public class ThemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<Product> productList;
    public final MainActivity activity;
    public List<ThemeViewHolder> viewHolders = new ArrayList<>();

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override @NonNull
    public ThemeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        ItemShopThemeBinding binding =
            DataBindingUtil.inflate(inflater, R.layout.item_shop_theme, viewGroup, false);

        binding.setTheme(activity.binding.getTheme());

        return new ThemeViewHolder(this, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        viewHolders.add((ThemeViewHolder) viewHolder);
        ((ThemeViewHolder) viewHolder).loadData(productList.get(position));
    }

    public ThemeAdapter(MainActivity mainActivity, List<Product> productList) {
        activity = mainActivity;

        productList.add(0, new Product(1, 0, 1));
        this.productList = productList;
    }
}