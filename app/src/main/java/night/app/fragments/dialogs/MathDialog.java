package night.app.fragments.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.activities.BarCodeActivity;
import night.app.databinding.DialogDreamBinding;
import night.app.databinding.DialogMathBinding;
import night.app.databinding.DialogMissionBinding;

public class MathDialog extends DialogFragment {
    DialogMathBinding binding;

    @Override
    public int getTheme() {
        return R.style.roundedDialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_math, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());


        return binding.getRoot();
    }
}
