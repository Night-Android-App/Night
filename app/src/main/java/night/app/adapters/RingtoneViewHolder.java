package night.app.adapters;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;

import night.app.data.Product;
import night.app.data.Ringtone;
import night.app.databinding.ItemRingtoneBinding;
import night.app.fragments.dialogs.PurchaseDialog;
import night.app.services.RingtonePlayer;

public class RingtoneViewHolder extends RecyclerView.ViewHolder {
    RingtoneAdapter adapter;
    ItemRingtoneBinding binding;
    String name;

    private void stopRingtone() {
        if (adapter.ringtonePlayer != null) {
            adapter.ringtonePlayer.release();
            adapter.ringtonePlayer = null;
        }

        binding.llItemRingtone
                .setBackgroundTintList(ColorStateList.valueOf(binding.getTheme().surface));

        binding.llItemRingtone.setOnClickListener(v -> playRingtone());
    }

    private void playRingtone() {
        binding.llItemRingtone.setBackgroundTintList(ColorStateList.valueOf(binding.getTheme().getSurfaceVariant()));

        if (adapter.ringtonePlayer != null) {
            adapter.viewHolders.get(adapter.ringtonePlayer.playerOwner).stopRingtone();
        }

        adapter.ringtonePlayer = new RingtonePlayer(adapter.activity, name, getAdapterPosition());
        adapter.ringtonePlayer.run();

        binding.llItemRingtone.setOnClickListener(v -> stopRingtone());
    }

    public void loadData(Product itemData) {
        binding.tvRingtoneName.setText(itemData.prodName);
        binding.tvShopItemPrice.setText(itemData.prodPrice + " coins");

        binding.button2.setBackgroundColor(binding.getTheme().accent);

        name = itemData.prodName;

        new Thread(() -> {
            Ringtone ringtone = adapter.activity.appDatabase.dao()
                    .getRingtone(itemData.prodName).get(0);

            adapter.activity.runOnUiThread(() -> {
                int hours = (int) Math.floor(ringtone.duration/60f);
                int minutes = ringtone.duration % 60;

                binding.tvRingtoneDuration.setText(LocalTime.of(hours, minutes).toString());

                binding.button2.setOnClickListener(v -> {
                    PurchaseDialog dialog = new PurchaseDialog();

                    Bundle bundle = new Bundle();
                    bundle.putString("name", itemData.prodName);
                    bundle.putString("price", String.valueOf(itemData.prodPrice));

                    dialog.setArguments(bundle);
                    dialog.show(adapter.activity.getSupportFragmentManager(), null);
                });

                binding.llItemRingtone.setOnClickListener(v -> playRingtone());
            });
        }).start();

    }

    public RingtoneViewHolder(RingtoneAdapter adapter, ItemRingtoneBinding binding) {
        super(binding.getRoot());

        this.adapter = adapter;
        this.binding = binding;
    }
}
