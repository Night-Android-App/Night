package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.DialogDreamBinding;

public class DreamDialog extends DialogFragment {
    DialogDreamBinding binding;

    public void updateDream() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_dream, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        binding.btnClose.setOnClickListener(v -> dismiss());


//        ViewGroup.LayoutParams params = binding.linearLayout6.getLayoutParams();
//        params.height = ((ScrollView) binding.linearLayout6.getParent()).getLayoutParams().height+200;
//        binding.linearLayout6.setLayoutParams(params);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
}
