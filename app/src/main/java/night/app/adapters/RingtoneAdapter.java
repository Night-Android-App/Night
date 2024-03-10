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
import night.app.databinding.ItemRingtoneBinding;
import night.app.services.RingtonePlayer;

public class RingtoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<Product> productList;
    public final MainActivity activity;
    final public RingtonePlayer ringtonePlayer;
    public List<RingtoneViewHolder> viewHolders = new ArrayList<>();

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override @NonNull
    public RingtoneViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        ItemRingtoneBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.item_ringtone, viewGroup, false);

        binding.setTheme(MainActivity.getAppliedTheme());

        return new RingtoneViewHolder(this, binding);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ((RingtoneViewHolder) viewHolder).loadData(productList.get(position));

        viewHolders.add((RingtoneViewHolder) viewHolder);

    }

    public RingtoneAdapter(MainActivity mainActivity, List<Product> productList) {
        activity = mainActivity;
        this.productList = productList;

        ringtonePlayer = new RingtonePlayer(activity);
    }
}