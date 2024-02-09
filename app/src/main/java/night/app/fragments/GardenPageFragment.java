package night.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import night.app.R;
import night.app.fragments.dialogs.ShopDialog;

public class GardenPageFragment extends Fragment {
    private void openShopMenu() {
        new ShopDialog().show(requireActivity().getSupportFragmentManager(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_garden_page, container, false);

        view.findViewById(R.id.btn_garden_shop).setOnClickListener(v -> openShopMenu());

        return view;
    }
}
