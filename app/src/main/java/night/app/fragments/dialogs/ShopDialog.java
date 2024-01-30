package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.adapters.ThemeItemAdapter;

public class ShopDialog extends DialogFragment {
    View view;

    private ArrayList<HashMap<String, String>> getSampleData() {
        HashMap<String, String> itemData = new HashMap<>() {{
            put("name", "test234");
            put("price", "40");
        }};

        ArrayList<HashMap<String, String>> array = new ArrayList<>() {{
            add(itemData);
            add(itemData);
        }};

        return array;
    }

    private void loadItemList(RecyclerView.Adapter adapter) {
        RecyclerView itemListView = view.findViewById(R.id.rv_shop_items);

        itemListView.removeAllViewsInLayout();

        itemListView.setAdapter(adapter);
    }

    private void initTabLayout() {
        TabLayout shopTab = view.findViewById(R.id.tab_shop);

        shopTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    // Themes tab
                    case 0 -> loadItemList(new ThemeItemAdapter(requireActivity(), getSampleData()));

                    // Ringtones tab
                    case 1 -> loadItemList(new ThemeItemAdapter(requireActivity(), getSampleData()));
                };
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_shop, container, false);

        view.findViewById(R.id.btn_shop_close).setOnClickListener(v -> dismiss());

        initTabLayout();

        RecyclerView itemListView = view.findViewById(R.id.rv_shop_items);
        itemListView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        loadItemList(new ThemeItemAdapter(requireActivity(), getSampleData()));

        return view;
    }
}

