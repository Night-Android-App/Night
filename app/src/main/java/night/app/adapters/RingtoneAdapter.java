package night.app.adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.entities.Product;
import night.app.databinding.HolderRingtoneViewBinding;
import night.app.utils.RingtonePlayer;

public class RingtoneAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Product[] productList;

    public final AppCompatActivity activity;
    final public RingtonePlayer ringtonePlayer;
    public List<RingtoneViewHolder> viewHolders = new ArrayList<>();

    @Override
    public int getItemCount() {
        return productList.length;
    }

    @Override @NonNull
    public RingtoneViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        HolderRingtoneViewBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.holder_ringtone_view, viewGroup, false);

        binding.setTheme(MainActivity.getAppliedTheme());

        return new RingtoneViewHolder(this, binding);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ((RingtoneViewHolder) viewHolder).loadData(productList[position]);

        viewHolders.add((RingtoneViewHolder) viewHolder);

    }

    public RingtoneAdapter(AppCompatActivity activity, Product[] productList) {
        this.activity = activity;
        this.productList = productList;

        ringtonePlayer = new RingtonePlayer(activity);
    }
}