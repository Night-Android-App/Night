package night.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.Alarm;
import night.app.databinding.ItemAlarmBinding;
import night.app.fragments.dialogs.ConfirmDialog;

public class AlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Alarm> alarmList;
    private List<AlarmViewHolder> viewHolders = new ArrayList<>();
    private List<Integer> selectedAlarms = new ArrayList<>();

    public final static int MODE_NORMAL = 0;
    public final static int MODE_SELECT = 1;

    public int mode = MODE_NORMAL;

    public int discardSelectedAlarms() {
        int total = selectedAlarms.size();

        selectedAlarms.sort(Collections.reverseOrder());

        List<Integer> id = new ArrayList<>();
        for (int adapterPos : selectedAlarms) {
            id.add(alarmList.get(adapterPos).id);
            alarmList.remove(adapterPos);
            notifyItemRemoved(adapterPos);
        }

        new Thread(() -> {
            MainActivity.getDatabase().dao().deleteAlarm(id);
        }).start();

//        selectedAlarms.clear();
        mode = MODE_NORMAL;

        return total;
    }

    public void selectAlarm(int adapterPos) {
        selectedAlarms.add(adapterPos);
    }

    public void unselectAlarm(int adapterPos) {
        for (int i=0; i < selectedAlarms.size(); i++) {
            if (selectedAlarms.get(i) == adapterPos) {
                selectedAlarms.remove(i);
                viewHolders.get(adapterPos).setNormalMode();
                if (selectedAlarms.size() == 0) {
                    mode = MODE_NORMAL;
                }
                return;
            }
        }
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewHolders.add((AlarmViewHolder) holder);
        ((AlarmViewHolder) holder).loadData(alarmList.get(position));
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        ItemAlarmBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.item_alarm, parent, false);

        return new AlarmViewHolder(this, binding);
    }

    public AlarmAdapter(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }
}
