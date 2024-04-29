package night.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.entities.Product;
import night.app.databinding.HolderThemeViewBinding;

public class ThemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Product[] products;
    public final MainActivity activity;
    public List<ThemeViewHolder> viewHolders = new ArrayList<>();

    @Override
    public int getItemCount() {
        return products.length;
    }

    @Override @NonNull
    public ThemeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        HolderThemeViewBinding binding =
            DataBindingUtil.inflate(inflater, R.layout.holder_theme_view, viewGroup, false);

        binding.setTheme(MainActivity.getAppliedTheme());

        return new ThemeViewHolder(this, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        ((ThemeViewHolder) holder).loadTheme();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ((ThemeViewHolder) viewHolder).loadData(products[position]);
    }

    public ThemeAdapter(MainActivity mainActivity, Product[] products) {
        activity = mainActivity;

        // add default theme item to shop page
        ArrayList<Product> productList = new ArrayList<>(Arrays.asList(products));
        productList.add(0, new Product(1, 0, 1));

        this.products = productList.toArray(new Product[0]);
    }
}