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
import night.app.data.Day;
import night.app.databinding.ItemDayRecordBinding;

public class DayItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    AppCompatActivity activity;
    List<Day> days;
    public int currMonth = -1;

    @Override
    public int getItemCount() {
        return days.size();
    }

    @Override @NonNull
    public DayItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        ItemDayRecordBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.item_day_record, viewGroup, false);

        binding.setTheme(MainActivity.getAppliedTheme());


        return new DayItemViewHolder(this, binding);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ((DayItemViewHolder) viewHolder).loadData(days.get(position));
    }

    public DayItemAdapter(AppCompatActivity activity, List<Day> days) {
        this.activity = activity;
        this.days = days;
    }
}
