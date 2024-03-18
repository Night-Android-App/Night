package night.app.fragments.clocks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import night.app.R;
import night.app.activities.SleepActivity;
import night.app.databinding.FragmentNapBinding;
import night.app.utils.TimeUtils;

public class NapFragment extends Fragment {
    private FragmentNapBinding binding;

    private int getNapMinutes() {
        return binding.npHrs.getValue() * 60 + binding.npMins.getValue();
    }

    private void handlePickerValueChanged(NumberPicker np, int old, int newValue) {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.HOUR, binding.npHrs.getValue());
        calendar.add(Calendar.MINUTE, binding.npMins.getValue());

        int hrValue = binding.npHrs.getValue();
        int minValue = binding.npMins.getValue();

        String totalNap = TimeUtils.toHrMinString(hrValue * 3600 + minValue * 60);

        updateSleepMsg("Alarm will ring at " + (calendar.get(Calendar.HOUR) + (calendar.get(Calendar.AM_PM) == Calendar.PM ? 12 : 0)) + ":" + (calendar.get(Calendar.MINUTE)), "Take a " + totalNap + " nap");
    }

    private void initNumberPicker() {
        binding.npHrs.setMinValue(0);
        binding.npHrs.setMaxValue(23);

        binding.npMins.setMinValue(0);
        binding.npMins.setMaxValue(59);

        binding.npHrs.setOnValueChangedListener(this::handlePickerValueChanged);
        binding.npMins.setOnValueChangedListener(this::handlePickerValueChanged);
    }

    private void updateSleepMsg(String upper, String lower) {
        Bundle bundle = new Bundle();
        bundle.putString("upperMsg", upper);
        bundle.putString("lowerMsg", lower);

        getParentFragmentManager().setFragmentResult("updateSleepInfo", bundle);
    }

    private void handleOnClickStart(View view) {
        Intent intent = new Intent(getActivity(), SleepActivity.class);
        intent.putExtra("sleepMinutes", getNapMinutes());
        startActivity(intent);
    }

    @Override @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_nap, container, false);

        initNumberPicker();
        updateSleepMsg("Nap with a one-time alarm", "Configure it below");

        binding.btnStart.setOnClickListener(this::handleOnClickStart);

        return binding.getRoot();
    }
}
