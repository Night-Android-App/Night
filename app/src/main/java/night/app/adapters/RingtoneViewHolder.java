package night.app.adapters;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import night.app.activities.MainActivity;
import night.app.data.entities.Product;
import night.app.data.entities.Ringtone;
import night.app.databinding.HolderRingtoneViewBinding;
import night.app.fragments.dialogs.PurchaseDialog;

public class RingtoneViewHolder extends RecyclerView.ViewHolder {
    private final RingtoneAdapter adapter;
    private final HolderRingtoneViewBinding binding;
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
            adapter.viewHolders.get(adapter.ringtonePlayer.playerOwner).stopRingtone();
        }

        adapter.ringtonePlayer.replaceRingtone(id, getAdapterPosition());
        adapter.ringtonePlayer.run();

        binding.llItemRingtone.setOnClickListener(v -> stopRingtone());
    }

    public void setOwned() {
        binding.button2.setText("OWNED");
        binding.button2.setEnabled(false);
    }

    public void loadData(Product itemData) {
        id = itemData.prodId;
//        binding.tvShopItemPrice.setText(itemData.price + " coins");

        new Thread(() -> {
            Ringtone ringtone = MainActivity.getDatabase().dao().getRingtone(itemData.prodId).get(0);
            adapter.activity.runOnUiThread(() -> {
                binding.tvRingtoneName.setText(ringtone.name);
                binding.llItemRingtone.setOnClickListener(v -> playRingtone());


                if (itemData.isBought == 1) setOwned();

                binding.button2.setOnClickListener(v -> {
                    PurchaseDialog<RingtoneViewHolder> dialog = new PurchaseDialog<>(this);

                    Bundle bundle = new Bundle();
                    bundle.putString("name", ringtone.name);
                    bundle.putString("price", String.valueOf(itemData.price));

                    dialog.setArguments(bundle);
                    dialog.show(adapter.activity.getSupportFragmentManager(), null);
                });
            });
        }).start();

    }

    public RingtoneViewHolder(RingtoneAdapter adapter, HolderRingtoneViewBinding binding) {
        super(binding.getRoot());

        this.adapter = adapter;
        this.binding = binding;
    }
}
