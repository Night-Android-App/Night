package night.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.Preferences;
import androidx.fragment.app.Fragment;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.DataStoreHelper;
import night.app.databinding.FragmentGardenPageBinding;
import night.app.fragments.dialogs.ShopDialog;

public class GardenPageFragment extends Fragment {
    public FragmentGardenPageBinding binding;

    private void showShopMenu() {
        if (getActivity() == null) return;
        new ShopDialog().show(getActivity().getSupportFragmentManager(), null);
    }

    private void loadData() {
        Preferences prefs = MainActivity.getDataStore().getPrefs();

        Integer preyCaught = prefs.get(DataStoreHelper.KEY_PREY_CAUGHT);
        binding.tvCoinsOwned.setText(preyCaught == null ? "N/A" : String.valueOf(preyCaught));

        Integer salePrice = prefs.get(DataStoreHelper.KEY_SALE_PRICE);
        binding.tvSalePrice.setText(salePrice == null ? "N/A" : String.valueOf(salePrice));

        Integer coinOwned = prefs.get(DataStoreHelper.KEY_COINS);
        binding.tvCoinsOwned.setText(coinOwned == null ? "0" : String.valueOf(coinOwned));

        Integer totalEarned = prefs.get(DataStoreHelper.KEY_TOTAL_EARNED);
        binding.tvTotalEarned.setText(totalEarned == null ? "0" : String.valueOf(totalEarned));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_garden_page, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        binding.btnGardenShop.setOnClickListener(v -> showShopMenu());
        loadData();

        return binding.getRoot();
    }
}
