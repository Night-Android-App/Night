package night.app.fragments.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import javax.xml.transform.Result;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.activities.BarCodeActivity;
import night.app.activities.SleepActivity;
import night.app.databinding.DialogDreamBinding;
import night.app.databinding.DialogMathBinding;
import night.app.databinding.DialogMissionBinding;

public class MathDialog extends DialogFragment {
    private DialogMathBinding binding;
    private final int preNum = (int) Math.floor(Math.random() * 90 + 10);
    private final int laterNum = (int) Math.floor(Math.random() * 90 + 10);

    private void checkAnswer() {
        String value = binding.etAnswer.getText().toString();

        if (!value.equals("")) {
            int answer = Integer.parseInt(value);
            if (preNum * laterNum == answer) {
                dismiss();
                ((SleepActivity) getActivity()).disableAlarm();
            }
        }
    }

    @Override
    public int getTheme() {
        return R.style.roundedDialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_math, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        binding.tvMathPre.setText(String.valueOf(preNum));
        binding.tvMathLater.setText(String.valueOf(laterNum));

        binding.btnPos.setOnClickListener(v -> checkAnswer());

        return binding.getRoot();
    }
}
