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
import night.app.data.entities.Day;
import night.app.databinding.HolderDayRecordViewBinding;

public class DayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    AppCompatActivity activity;
    List<Day> days;
    public int currMonth = -1;

    @Override
    public int getItemCount() {
        return days.size();
    }

    @Override @NonNull
    public DayViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        HolderDayRecordViewBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.holder_day_record_view, viewGroup, false);

        binding.setTheme(MainActivity.getAppliedTheme());


        return new DayViewHolder(this, binding);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ((DayViewHolder) viewHolder).loadData(days.get(position));
    }

    public DayAdapter(AppCompatActivity activity, List<Day> days) {
        this.activity = activity;
        this.days = days;
    }
}
