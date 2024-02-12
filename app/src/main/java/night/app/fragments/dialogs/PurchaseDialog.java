package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.DialogFragment;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.DialogPurchaseBinding;

public class PurchaseDialog extends DialogFragment {
    DialogPurchaseBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        requireDialog().getWindow()
            .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_purchase, container, false);
        binding.setTheme(((MainActivity) requireActivity()).theme);

        Integer price = Integer.parseInt(requireArguments().getString("price", "No value"));
        Integer coins = ((MainActivity) requireActivity()).dataStore.getPrefs().get(PreferencesKeys.intKey("coins"));

        binding.tvPurchaseItemPrice.setText(String.valueOf(price));
        binding.tvPurchaseCoins.setText(String.valueOf(coins == null ? coins = 0 : coins));

        binding.tvPurchaseRemain.setText(String.valueOf(coins - price));
        binding.tvPurchaseName.setText(getArguments().getString("name", ""));

        // button operation
        if (Integer.parseInt(binding.tvPurchaseRemain.getText().toString()) < 0) {
            // disable positive button if user has no enough coins
            binding.btnPositive.setEnabled(false);
        }
        else {
            // purchase operation
        }

        binding.btnNegative.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }
}

