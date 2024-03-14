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
    public DialogConfirmBinding binding;

    String dialogTitle;
    String dialogDesc;
    Function handleOnClickPositiveButton;

    public interface Function {
        void run(ConfirmDialog dialog);
    }

    public void loadContent() {
        binding.tvTitle.setText(dialogTitle);
        binding.tvDesc.setText(dialogDesc);

        if (handleOnClickPositiveButton == null) {
            binding.btnNegative.setVisibility(View.GONE);
            binding.btnPositive.setText("OK");
            binding.btnPositive.setOnClickListener(v -> dismiss());
        }

        binding.btnPositive.setOnClickListener(v -> {
            if (handleOnClickPositiveButton == null) {
                dismiss();
                return;
            }
            handleOnClickPositiveButton.run(this);
        });
    }

    public void showLoading() {
        binding.llConfirm.setVisibility(View.GONE);
        binding.pbLoading.setVisibility(View.VISIBLE);
    }

    public void showMessage() {
        binding.llConfirm.setVisibility(View.VISIBLE);
        binding.pbLoading.setVisibility(View.GONE);
    }

    public void replaceContent(String title, String desc, Function fn) {
        dialogTitle = title;
        dialogDesc = desc;
        handleOnClickPositiveButton = fn;
        loadContent();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_confirm, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        loadContent();
        binding.btnNegative.setOnClickListener(v -> dismiss());
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
