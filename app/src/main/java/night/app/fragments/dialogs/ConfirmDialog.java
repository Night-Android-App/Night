package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.DialogConfirmBinding;

public class ConfirmDialog extends DialogFragment {
    DialogConfirmBinding binding;

    String dialogTitle;
    String dialogDesc;
    Function handleOnClickPositiveButton;

    public interface Function {
        void run();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_confirm, container, false);
        binding.setTheme(activity.theme);

        binding.tvTitle.setText(dialogTitle);
        binding.tvDesc.setText(dialogDesc);

        if (handleOnClickPositiveButton == null) {
            binding.btnNegative.setVisibility(View.GONE);
            binding.btnPositive.setText("OK");
        }

        binding.btnPositive.setOnClickListener(v -> {
            if (handleOnClickPositiveButton != null) {
                handleOnClickPositiveButton.run();
            }

            dismiss();
        });

        return binding.getRoot();
    }

    @Override
    public int getTheme() {
        return R.style.roundedDialog;
    }

    public ConfirmDialog(String title, String desc, Function fn) {
        dialogTitle = title;
        dialogDesc = desc;
        handleOnClickPositiveButton = fn;
    }
}
