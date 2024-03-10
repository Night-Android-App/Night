package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.DialogAgreementBinding;

public class AgreementDialog extends DialogFragment {
    DialogAgreementBinding binding;

    private void loadFileContent(String path) {
        BufferedReader reader = null;
        try {
            InputStream inputStream = requireActivity().getAssets().open(path);
            reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder policyContent = new StringBuilder();
            String aRow;
            while ((aRow = reader.readLine()) != null) {
                policyContent.append(aRow).append("\n");
            }

            binding.tvPolicyAgreedDate.setText(policyContent.toString());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
    }

    private void setOnTabSelectListener() {
        binding.tabAgree.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadFileContent("terms_of_service.txt");
                    return;
                }
                loadFileContent("privacy_policy.txt");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_agreement, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        requireDialog().getWindow().setStatusBarColor(binding.getTheme().getPrimary());
        requireDialog().getWindow().setNavigationBarColor(binding.getTheme().getSecondary());

        loadFileContent("terms_of_service.txt");
        setOnTabSelectListener();


        String agreedDate = MainActivity.getDataStore().getPrefs().get(PreferencesKeys.stringKey("PolicyAgreedDate"));

        if (agreedDate != null) {
            binding.cbAgree.setEnabled(false);
            binding.cbAgree.setChecked(true);
            binding.cbAgree.setText("You agreed to the agreements above.");
            binding.btnPos.setOnClickListener(v -> dismiss());
            binding.btnPos.setText("CLOSE");

            return binding.getRoot();
        }

        binding.btnPos.setOnClickListener(v -> {
            if (!binding.cbAgree.isChecked()) {
                binding.cbAgree.setError("You need to agree for using our services.");
                return;
            }

            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.now(), zoneId);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDateTime = formatter.format(zonedDateTime);

            MainActivity.getDataStore().update(
                    PreferencesKeys.stringKey("PolicyAgreedDate"),
                    formattedDateTime
            );

            MainActivity.getDataStore().update(
                    PreferencesKeys.booleanKey("isFirstVisited"),
                    true
            );

            dismiss();
            ((MainActivity) requireActivity()).requestPermissions();
        });


        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
}
