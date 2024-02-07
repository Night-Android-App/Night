package night.app.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import night.app.data.Product;
import night.app.data.Ringtone;
import night.app.databinding.ItemRingtoneBinding;

public class RingtoneViewHolder extends RecyclerView.ViewHolder {

    public void loadData(Product itemData) {

    }

    public RingtoneViewHolder(RingtoneAdapter adapter, ItemRingtoneBinding binding) {
        super(binding.getRoot());
    }
}
