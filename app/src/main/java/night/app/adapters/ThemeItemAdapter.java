package night.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import night.app.R;

public class ThemeItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final JSONObject[] localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvItemName;
        private final TextView tvItemPrice;

        public ViewHolder(View view) {
            super(view);
            tvItemName = view.findViewById(R.id.tv_shop_item_name);
            tvItemPrice = view.findViewById(R.id.tv_shop_item_price);
        }

        public void loadData(JSONObject itemData) throws JSONException {
            tvItemName.setText(itemData.getString("name"));
            tvItemPrice.setText(itemData.getString("price"));
        }
    }

    public ThemeItemAdapter(JSONObject[] dataSet) {
        localDataSet = dataSet;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_shop_theme, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {

        try {
            ((ViewHolder) viewHolder).loadData(localDataSet[position]);
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}
