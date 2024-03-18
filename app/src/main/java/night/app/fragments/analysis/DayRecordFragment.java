package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.List;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.AppDAO;
import night.app.data.Day;
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

    private void loadDay(int date) {
        if (getActivity() == null) return;

        new Thread(() -> {
            AppDAO dao = MainActivity.getDatabase().dao();
            List<Day> dayList = dao.getDayByDate(date);

            getActivity().runOnUiThread(() -> loadDay(dayList));
        }).start();
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

    private void loadDay(List<Day> dayList) {
        if (dayList.size() == 0) {
            setStyleForEmptyRecord();
            return;
        }

        Day day = dayList.get(0);
        SleepData sleepData = day.sleep == null ? null : new SleepData(day.sleep);
        setUpperPanelResult(TimeUtils.toDateString(day.date, true), sleepData);

        int totalLightSleep = sleepData.getTotalSecondsByConfidence(50, 75);
        binding.tvLightSleep.setText(TimeUtils.toHrMinString(totalLightSleep));

        int totalDeepSleep = sleepData.getTotalSecondsByConfidence(75, 101);
        binding.tvDeepSleep.setText(TimeUtils.toHrMinString(totalDeepSleep));

        binding.tvInBed.setText(TimeUtils.toHrMinString(sleepData.getTotalSleep()));
        binding.tvAnalDream.setText(day.dream == null ? "No record" : day.dream);

        Integer[] data = sleepData.getConfidences();

        Integer[] timelines = sleepData.getTimelines();
        String[] hrMin = new String[timelines.length];
        for (int i=0; i < timelines.length; i++) {
            hrMin[i] = TimeUtils.toTimeNotation(timelines[i]*60);
        }

        new ChartBuilder<>(binding.lineChartDayRecord, hrMin, data)
                .setTheme(binding.getTheme())
                .invalidate();
    }

    private void setLoadDayResultListener() {
        if (!isAdded()) return;

        getParentFragmentManager()
                .setFragmentResultListener("loadDay", this, (String key, Bundle bundle) -> {
                    int date = bundle.getInt("date", 0);

                    if (date == 0) {
                        loadDay(Sample.getDay());
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

        loadDay(Sample.getDay());
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

        if (getActivity() != null) {
            getActivity().findViewById(R.id.iv_left)
                    .setOnClickListener(v -> toPreviousDay());

            getActivity().findViewById(R.id.iv_right)
                    .setOnClickListener(v -> toNextDay());

            loadDay(TimeUtils.getToday());
            setStylesForNormalMode();
        }

        return binding.getRoot();
    }
}
