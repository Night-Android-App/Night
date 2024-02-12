package night.app.fragments.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.adapters.ThemeAdapter;
import night.app.databinding.FragmentRecycleViewBinding;

public class MonthRecordFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RecyclerView view = (RecyclerView) inflater.inflate(R.layout.fragment_recycle_view, container, false);

        view.setLayoutManager(new LinearLayoutManager(requireActivity()));

        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        bundle.putString("info1", "N/A");
        bundle.putString("info2", "N/A");
        getParentFragmentManager().setFragmentResult("updateAnalytics", bundle);

        return view;
    }
}
