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
    final public static int TYPE_TERMS = 0;
    final public static int TYPE_PRIVACY = 1;

    private DialogAgreementBinding binding;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_agreement, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        requireDialog().getWindow().setStatusBarColor(binding.getTheme().getPrimary());
        requireDialog().getWindow().setNavigationBarColor(binding.getTheme().getSecondary());

        if (requireArguments().getInt("type") == TYPE_TERMS) {
            loadFileContent("terms_of_service.txt");
            binding.tvTitle.setText("Terms of Service");
        }
        else if (requireArguments().getInt("type") == TYPE_PRIVACY) {
            loadFileContent("privacy_policy.txt");
            binding.tvTitle.setText("Privacy policy");
        }

        binding.btnPos.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
}
