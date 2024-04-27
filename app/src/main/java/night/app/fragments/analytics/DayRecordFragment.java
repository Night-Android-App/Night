package night.app.fragments.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.AppDatabase;
import night.app.data.entities.Day;
import night.app.data.entities.SleepEvent;
import night.app.databinding.FragmentDayRecordBinding;
import night.app.services.ChartBuilder;
import night.app.services.SleepData;
import night.app.utils.TimeUtils;

public class DayRecordFragment extends Fragment {
    private FragmentDayRecordBinding binding;
    private long date = TimeUtils.getTodayAtMidNight();

    private void setUpperPanelResult(String date, SleepData sleepData) {
        Bundle bundle = new Bundle();

        bundle.putInt("type", 0);
        bundle.putString("date", date);

        if (sleepData != null) {
            bundle.putInt("score", Math.round(sleepData.getScore()));
            bundle.putDouble("info1", sleepData.getFellAsleepDuration());
            bundle.putDouble("info2", sleepData.getSleepEfficiency());
        }

        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
    }

    private void setStyleForEmptyRecord() {
        binding.tvLightSleep.setText("N/A");
        binding.tvDeepSleep.setText("N/A");
        binding.tvInBed.setText("N/A");

        new ChartBuilder<>(binding.lineChartDayRecord, null, null).invalidate();

        setUpperPanelResult(TimeUtils.toDateString(date, true), null);
    }

    private void loadDay(long timestamp) {
        setUpperPanelResult(TimeUtils.toDateString(timestamp, true), null);

        AppDatabase db = MainActivity.getDatabase();

        new Thread(() -> {
            Day day = db.dayDAO().getDayByDate(timestamp);

            if (day == null) day = new Day();

            SleepEvent[] events = db.sleepEventDAO().getByRange(day.date, day.startTime, day.endTime);
            SleepData data = new SleepData(events);

            System.out.println(events.length);
            // load chart
            new ChartBuilder<>(binding.lineChartDayRecord, data.getTimelines(), data.getConfidences())
                    .invalidate();

            binding.tvLightSleep.setText(TimeUtils.toHrMinString((int) data.getConfidenceDuration(50,74)));
            binding.tvDeepSleep.setText(TimeUtils.toHrMinString((int) data.getConfidenceDuration(75,100)));
            binding.tvInBed.setText(TimeUtils.toHrMinString((int) data.getInBedDuration()));

            setUpperPanelResult(TimeUtils.toDateString(timestamp, true), data);
        }).start();
    }

    private void setLoadDayListener() {
        getParentFragmentManager()
                .setFragmentResultListener("loadDay", this, (String key, Bundle bundle) -> {
                    int date = bundle.getInt("date", 0);

                    if (date == 0) {
//                        loadDay(Sample.getDay());
                        setStylesForSampleMode();
                        return;
                    }

                    loadDay(date);
                    setStylesForNormalMode();
                });
    }

    private void loadDayByDiff(int diff) {
        date = TimeUtils.dayAdd(date, diff);
        loadDay(date);

        setStylesForNormalMode();
    }

    private void setStylesForSampleMode() {
        if (getActivity() == null) return;

        ImageView ivLeft = getActivity().findViewById(R.id.iv_left);
        ImageView ivRight = getActivity().findViewById(R.id.iv_right);

//        loadDay(Sample.getDay());
        ivLeft.setVisibility(View.GONE);
        ivRight.setVisibility(View.GONE);
    }

    private void setStylesForNormalMode() {
        ImageView ivLeft = getActivity().findViewById(R.id.iv_left);
        ImageView ivRight = getActivity().findViewById(R.id.iv_right);

        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);


        if (TimeUtils.dayAdd(date, 1) > TimeUtils.getTodayAtMidNight()) {
            ivRight.setVisibility(View.GONE);
        }
        else if (date <= TimeUtils.dayAdd(TimeUtils.getTodayAtMidNight(), -29)) {
            ivLeft.setVisibility(View.GONE);
        }

        setLoadDayListener();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_day_record, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        getActivity().findViewById(R.id.iv_left).setOnClickListener(v -> loadDayByDiff(-1));
        getActivity().findViewById(R.id.iv_right).setOnClickListener(v -> loadDayByDiff(+1));

        loadDay(TimeUtils.getTodayAtMidNight());
        setStylesForNormalMode();

        if (getArguments() != null) {
            int mode = getArguments().getInt("mode", 0);

            if (mode == AnalyticsPageFragment.MODE_SAMPLE) {
                setStylesForSampleMode();
                return binding.getRoot();
            }
        }

        return binding.getRoot();
    }
}
