package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import night.app.R;
import night.app.adapters.ThemeItemAdapter;

public class ShopDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_shop, container, false);

        view.findViewById(R.id.btn_shop_close).setOnClickListener(v -> dismiss());

        RecyclerView itemListView = view.findViewById(R.id.rv_shop_items);
        itemListView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        itemListView.setAdapter(new ThemeItemAdapter(new String[]{"test1", "test2"}));
        return view;
    }
}

