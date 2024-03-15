package night.app.adapters;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import night.app.R;
import night.app.data.Day;
import night.app.databinding.ItemDayRecordBinding;
import night.app.services.SleepData;
import night.app.utils.TimeUtils;

public class DayItemViewHolder extends RecyclerView.ViewHolder {
    DayItemAdapter adapter;
    ItemDayRecordBinding binding;
    Day day;

    private void handleOnClickItem(View view) {
        ((TabLayout) adapter.activity.findViewById(R.id.tab_anal)).getTabAt(0).select();

        Bundle bundle = new Bundle();
        bundle.putInt("date", day.date);
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
        calendar.setTime(Date.from(Instant.ofEpochSecond(day.date)));

        decideIsMonthLabelEnable(calendar);

        String[] days = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };


        binding.tvDay.setText(calendar.get(Calendar.DAY_OF_MONTH) + getDayOfMonthSuffix(calendar.get(Calendar.DAY_OF_MONTH)));
        binding.tvWeekOfDay.setText("(" + days[calendar.get(Calendar.DAY_OF_WEEK)] + ")");


        SleepData sleepData = new SleepData(day.sleep);
        binding.tvSleepHrs.setText(TimeUtils.toHrMinString(sleepData.getTotalSleep()));

        binding.tvSleepEfficiency.setText(Math.round(sleepData.getSleepEfficiency() * 100) + "%");
    }

    public DayItemViewHolder(DayItemAdapter adapter, ItemDayRecordBinding binding) {
        super(binding.getRoot());

        this.adapter = adapter;
        this.binding = binding;
    }
}
