package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.DialogPurchaseBinding;

public class PurchaseDialog extends DialogFragment {
    DialogPurchaseBinding binding;

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow()
            .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_purchase, container, false);
        binding.setTheme(((MainActivity) requireActivity()).theme);
        return binding.getRoot();
    }
}

