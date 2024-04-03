package night.app.adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.entities.Ringtone;
import night.app.databinding.ItemRingtoneBinding;
import night.app.services.RingtonePlayer;

public class RingtoneOwnedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<Ringtone> ringtoneList;

    public final AppCompatActivity activity;
    final public RingtonePlayer ringtonePlayer;

    @Override
    public int getItemCount() {
        return ringtoneList.size();
    }

    @Override @NonNull
    public RingtoneOwnedViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        ItemRingtoneBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.item_ringtone, viewGroup, false);

        binding.setTheme(MainActivity.getAppliedTheme());

        return new RingtoneOwnedViewHolder(this, binding);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ((RingtoneOwnedViewHolder) viewHolder).loadData(ringtoneList.get(position));

    }

    public RingtoneOwnedAdapter(AppCompatActivity activity, List<Ringtone> ringtoneList) {
        this.activity = activity;
        this.ringtoneList = ringtoneList;

        ringtonePlayer = new RingtonePlayer(activity);
    }
}