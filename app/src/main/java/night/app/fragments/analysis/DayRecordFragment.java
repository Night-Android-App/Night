package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.dao.AppDAO;
import night.app.data.dao.DayDAO;
import night.app.data.entities.Day;
import night.app.data.entities.SleepEvent;
import night.app.databinding.FragmentDayRecordBinding;
import night.app.fragments.AnalysisPageFragment;
import night.app.services.ChartBuilder;
import night.app.services.Sample;
import night.app.services.SleepData;
import night.app.utils.TimeUtils;

public class DayRecordFragment extends Fragment {
    private FragmentDayRecordBinding binding;
    private int date = TimeUtils.getToday();

    private void setUpperPanelResult(String date, SleepData sleepData) {
        Bundle bundle = new Bundle();

        bundle.putInt("type", 0);
        bundle.putString("date", date);

        if (sleepData != null) {
            bundle.putInt("score", Math.round(sleepData.getScore()));
            bundle.putDouble("info1", sleepData.getFellAsleepTime());
            bundle.putDouble("info2", sleepData.getSleepEfficiency());
        }

        if (!isAdded()) return;
        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);
    }

    private void setStyleForEmptyRecord() {
        binding.tvLightSleep.setText("N/A");
        binding.tvDeepSleep.setText("N/A");
        binding.tvInBed.setText("N/A");

        new ChartBuilder<>(binding.lineChartDayRecord, null, null)
                .setTheme(binding.getTheme())
                .invalidate();

        setUpperPanelResult(TimeUtils.toDateString(date, true), null);
    }

    private void loadDay(long timestamp) {
        new Thread(() -> {
            SleepEvent[] events = MainActivity.getDatabase().sleepEventDAO().getEventsByDay(timestamp);

            String[] time = new String[events.length];
            Integer[] confidence = new Integer[events.length];
            for (int i=0; i < events.length; i++) {
                confidence[i] = events[i].confidence;

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(events[i].timeline * 1000);

                time[i] = calendar.get(Calendar.HOUR_OF_DAY) + ":" +calendar.get(Calendar.MINUTE);
            }

            System.out.println("Confidence List: " + Arrays.toString(confidence));
            System.out.println("Timelines: " + Arrays.toString(time));

            new ChartBuilder<>(binding.lineChartDayRecord, time, confidence)
                    .setTheme(binding.getTheme())
                    .invalidate();

            int light = 0;
            int deep = 0;
            for (int confi : confidence) {
                if (confi >= 50 && confi < 75) {

                }
            }


                setUpperPanelResult(TimeUtils.toDateString(timestamp, true), null);
        }).start();


//        int totalLightSleep = sleepData.getTotalSecondsByConfidence(50, 75);
//        binding.tvLightSleep.setText(TimeUtils.toHrMinString(totalLightSleep));
//
//        int totalDeepSleep = sleepData.getTotalSecondsByConfidence(75, 101);
//        binding.tvDeepSleep.setText(TimeUtils.toHrMinString(totalDeepSleep));
//
//        binding.tvInBed.setText(TimeUtils.toHrMinString(sleepData.getTotalSleep()));
//        binding.tvAnalDream.setText(day.dream == null ? "No record" : day.dream);
//
//        Integer[] data = sleepData.getConfidences();
//
//        Integer[] timelines = sleepData.getTimelines();
//        String[] hrMin = new String[timelines.length];
//        for (int i=0; i < timelines.length; i++) {
//            hrMin[i] = TimeUtils.toTimeNotation(timelines[i]*60);
//        }
//
//        new ChartBuilder<>(binding.lineChartDayRecord, hrMin, data)
//                .setTheme(binding.getTheme())
//                .invalidate();
    }

    private void setLoadDayResultListener() {
        if (!isAdded()) return;

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

    private void toPreviousDay() {
        date = TimeUtils.dayAdd(date, -1);
        loadDay(date);

        setStylesForNormalMode();
    }

    private void toNextDay() {
        date = TimeUtils.dayAdd(date, 1);
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
        if (getActivity() == null) return;

        ImageView ivLeft = getActivity().findViewById(R.id.iv_left);
        ImageView ivRight = getActivity().findViewById(R.id.iv_right);

        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);


        if (TimeUtils.dayAdd(date, 1) > TimeUtils.getToday()) {
            ivRight.setVisibility(View.GONE);
        }
        else if (date <= TimeUtils.dayAdd(TimeUtils.getToday(), -29)) {
            ivLeft.setVisibility(View.GONE);
        }

        setLoadDayResultListener();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_day_record, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        if (getArguments() != null) {
            int mode = getArguments().getInt("mode", 0);

            if (mode == AnalysisPageFragment.MODE_SAMPLE) {
                setStylesForSampleMode();
                return binding.getRoot();
            }
        }

        getActivity().findViewById(R.id.iv_left)
                .setOnClickListener(v -> toPreviousDay());

        getActivity().findViewById(R.id.iv_right)
                .setOnClickListener(v -> toNextDay());

        loadDay(TimeUtils.getToday());
        setStylesForNormalMode();

        return binding.getRoot();
    }
}
