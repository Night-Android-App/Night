package night.app.adapters;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import night.app.data.Product;
import night.app.data.Ringtone;
import night.app.databinding.ItemRingtoneBinding;
import night.app.services.DateTimeFormatter;
import night.app.services.RingtonePlayer;

public class RingtoneViewHolder extends RecyclerView.ViewHolder {
    RingtoneAdapter adapter;
    ItemRingtoneBinding binding;

    public void loadData(Product itemData) {
        binding.tvRingtoneName.setText(itemData.prodName);
        binding.tvShopItemPrice.setText(itemData.prodPrice + " coins");

        new Thread(() -> {
            Ringtone ringtone = adapter.activity.appDatabase.dao()
                    .getRingtone(itemData.prodName).get(0);

            adapter.activity.runOnUiThread(() -> {
                binding.tvRingtoneDuration.setText(DateTimeFormatter.convertIntTime(ringtone.duration));
            });

            binding.button2.setOnClickListener(v -> {
                if (adapter.ringtonePlayer != null) {
                    adapter.ringtonePlayer.release();
                }
                adapter.ringtonePlayer = new RingtonePlayer(adapter.activity, itemData.prodName);
                adapter.ringtonePlayer.run();
            });
        }).start();

    }

    public RingtoneViewHolder(RingtoneAdapter adapter, ItemRingtoneBinding binding) {
        super(binding.getRoot());

        this.adapter = adapter;
        this.binding = binding;
    }
}
