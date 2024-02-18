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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.DialogPrivacyPolicyBinding;

public class PrivacyPolicyDialog extends DialogFragment {
    DialogPrivacyPolicyBinding binding;

    private void loadPolicyContent() {
        MainActivity activity = (MainActivity) requireActivity();

        BufferedReader reader = null;
        try {
            InputStream inputStream = activity.getAssets().open("privacy_policy.txt");
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

    private void setOnBackKeyListener() {
        requireDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                System.exit(0);
                return true;
            }
            return false;
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_privacy_policy, container, false);
        binding.setTheme(activity.theme);

        requireDialog().getWindow().setStatusBarColor(binding.getTheme().getPrimary());
        requireDialog().getWindow().setNavigationBarColor(binding.getTheme().getSecondary());

        loadPolicyContent();

        String agreedDate = activity.dataStore.getPrefs().get(PreferencesKeys.stringKey("PolicyAgreedDate"));

        if (agreedDate != null) {
            binding.checkBox.setEnabled(false);
            binding.checkBox.setChecked(true);
            binding.checkBox.setText("You agreed the privacy policy at " + agreedDate);
            binding.button3.setOnClickListener(v -> dismiss());

            return binding.getRoot();
        }

        setOnBackKeyListener();

        binding.button3.setOnClickListener(v -> {
            if (!binding.checkBox.isChecked()) {
                binding.checkBox.setError("You need to agree to proceed");
                return;
            }

            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.now(), zoneId);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDateTime = formatter.format(zonedDateTime);

            activity.dataStore.update(
                    PreferencesKeys.stringKey("PolicyAgreedDate"),
                    formattedDateTime
            );

            activity.dataStore.update(
                    PreferencesKeys.booleanKey("isFirstVisited"),
                    true
            );

            dismiss();
        });


        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
}
