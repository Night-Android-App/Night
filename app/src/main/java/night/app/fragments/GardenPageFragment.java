package night.app.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.Fragment;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.FragmentGardenPageBinding;
import night.app.fragments.dialogs.ShopDialog;

public class GardenPageFragment extends Fragment {
    public FragmentGardenPageBinding binding;

    private void showShopMenu() {
        new ShopDialog().show(requireActivity().getSupportFragmentManager(), null);
    }

    private void loadData() {
        Preferences prefs = MainActivity.getDataStore().getPrefs();

        Integer preyCaught = prefs.get(PreferencesKeys.intKey("preyCaught"));
        binding.tvCoinsOwned.setText(preyCaught == null ? "N/A" : String.valueOf(preyCaught));

        Integer salePrice = prefs.get(PreferencesKeys.intKey("salePrice"));
        binding.tvSalePrice.setText(salePrice == null ? "N/A" : String.valueOf(salePrice));

        Integer coinOwned = prefs.get(PreferencesKeys.intKey("coins"));
        binding.tvCoinsOwned.setText(coinOwned == null ? "0" : String.valueOf(coinOwned));

        Integer totalEarned = prefs.get(PreferencesKeys.intKey("totalEarned"));
        binding.tvTotalEarned.setText(totalEarned == null ? "0" : String.valueOf(totalEarned));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_garden_page, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        binding.btnGardenShop.setOnClickListener(v -> showShopMenu());
        loadData();

        return binding.getRoot();
    }
}
