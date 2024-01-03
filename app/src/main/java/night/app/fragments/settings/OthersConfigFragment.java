package night.app.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import night.app.R;

public class OthersConfigFragment extends Fragment {
    public void requestNotificationPermission() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", requireActivity().getPackageName(), null));
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_others_config, container, false);

        view.findViewById(R.id.grantNotificationPermission)
            .setOnClickListener(v -> requestNotificationPermission());

        return view;
    }
}
