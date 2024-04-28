package night.app.adapters;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.entities.Day;
import night.app.data.entities.SleepEvent;
import night.app.databinding.HolderDayRecordViewBinding;
import night.app.utils.SleepAnalyser;
import night.app.utils.DatetimeUtils;

public class DayViewHolder extends RecyclerView.ViewHolder {
    DayAdapter adapter;
    HolderDayRecordViewBinding binding;
    Day day;

    private void handleOnClickItem(View view) {
        ((TabLayout) adapter.activity.findViewById(R.id.tab_anal)).getTabAt(0).select();

        Bundle bundle = new Bundle();
        bundle.putLong("date", day.date);
        adapter.activity.getSupportFragmentManager().setFragmentResult("loadDay", bundle);
    }

    private String getDayOfMonthSuffix(int day) {
        return switch (day % 10) {
            case 1 -> day == 11 ? "th" : "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default ->  "th";
        };
    }

    private void decideIsMonthLabelEnable(Calendar calendar) {
        if (calendar.get(Calendar.MONTH) != adapter.currMonth) {
            adapter.currMonth = calendar.get(Calendar.MONTH);

            binding.tvCategoryName.setText(new SimpleDateFormat("MMMM", Locale.ENGLISH).format(calendar.getTime()));
            binding.tvCategoryName.setVisibility(View.VISIBLE);
        }
    }

    public void loadData(Day day) {
        this.day = day;
        binding.clItemDay.setOnClickListener(this::handleOnClickItem);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(day.date);

        decideIsMonthLabelEnable(calendar);

        String[] days = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

        binding.tvDay.setText(calendar.get(Calendar.DAY_OF_MONTH) + getDayOfMonthSuffix(calendar.get(Calendar.DAY_OF_MONTH)));
        binding.tvWeekOfDay.setText("(" + days[calendar.get(Calendar.DAY_OF_WEEK)-1] + ")");

        new Thread(() -> {
            SleepEvent[] events = MainActivity.getDatabase().sleepEventDAO().get(day.date, day.startTime, day.endTime);
            SleepAnalyser sleepData = new SleepAnalyser(events);

            adapter.activity.runOnUiThread(() -> {
                binding.tvSleepHrs.setText(DatetimeUtils.toHrMinString(sleepData.getConfidenceDuration(50, 100)));

                binding.tvSleepEfficiency.setText(Math.round(sleepData.getSleepEfficiency() * 100) + "%");
            });
        }).start();
    }

    public DayViewHolder(DayAdapter adapter, HolderDayRecordViewBinding binding) {
        super(binding.getRoot());

        this.adapter = adapter;
        this.binding = binding;
    }
}
