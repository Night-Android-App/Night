package night.app.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import night.app.activities.RingtoneActivity;
import night.app.data.entities.Ringtone;
import night.app.databinding.ItemRingtoneBinding;

public class RingtoneOwnedViewHolder extends RecyclerView.ViewHolder {
    private final RingtoneOwnedAdapter adapter;
    private final ItemRingtoneBinding binding;
    private int id;

    private void stopRingtone() {
        if (adapter.ringtonePlayer != null) adapter.ringtonePlayer.release();

        binding.llItemRingtone
                .setBackgroundTintList(ColorStateList.valueOf(binding.getTheme().getSurface()));

        binding.llItemRingtone.setOnClickListener(v -> playRingtone());
    }

    private void playRingtone() {
        binding.llItemRingtone.setBackgroundTintList(ColorStateList.valueOf(binding.getTheme().getSurfaceVariant()));

        if (adapter.ringtonePlayer.playerOwner != null) {
//            adapter.viewHolders.get(adapter.ringtonePlayer.playerOwner).stopRingtone();
        }

        adapter.ringtonePlayer.replaceRingtone(id, getAdapterPosition());
        adapter.ringtonePlayer.run();

        binding.llItemRingtone.setOnClickListener(v -> stopRingtone());
    }

    public void loadData(Ringtone itemData) {
        binding.tvShopItemPrice.setVisibility(View.GONE);

        binding.tvRingtoneName.setText(itemData.name);

        binding.button2.setText("Apply");
        binding.button2.setOnClickListener(v -> adapter.activity.finish());

        binding.button2.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.putExtra("ringtoneId", itemData.prodId);
            adapter.activity.setResult(Activity.RESULT_OK, intent);

            ((RingtoneActivity) adapter.activity).selectRingtone(itemData.prodId);
        });
//                binding.llItemRingtone.setOnClickListener(v -> playRingtone());
    }

    public RingtoneOwnedViewHolder(RingtoneOwnedAdapter adapter, ItemRingtoneBinding binding) {
        super(binding.getRoot());

        this.adapter = adapter;
        this.binding = binding;
    }
}
