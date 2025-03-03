package night.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.entities.Alarm;
import night.app.databinding.HolderAlarmViewBinding;

public class AlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Alarm> alarmList;
    private List<AlarmViewHolder> viewHolders = new ArrayList<>();
    private List<Integer> selectedAlarms = new ArrayList<>();
    public AppCompatActivity activity;

    private Fragment fr;
    public final static int MODE_NORMAL = 0;
    public final static int MODE_SELECT = 1;

    public int mode = MODE_NORMAL;

    public int discardSelectedAlarms() {
        int total = selectedAlarms.size();

        selectedAlarms.sort(Collections.reverseOrder());

        List<Integer> id = new ArrayList<>();
        for (int adapterPos : selectedAlarms) {
            if (alarmList.size() > adapterPos) {
                id.add(alarmList.get(adapterPos).id);
                alarmList.remove(adapterPos);
                notifyItemRemoved(adapterPos);
            }
        }

        new Thread(() -> {
            MainActivity.getDatabase().alarmDAO().discard(id);
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

        HolderAlarmViewBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.holder_alarm_view, parent, false);

        return new AlarmViewHolder(this, binding, fr);
    }

    public AlarmAdapter(AppCompatActivity activity, Fragment fr, List<Alarm> alarmList) {
        this.activity = activity;
        this.alarmList = alarmList;
        this.fr = fr;
    }
}
