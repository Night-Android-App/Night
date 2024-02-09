package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import org.json.JSONObject;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.DialogRegisterBinding;
import night.app.networks.AccountRequest;
import night.app.services.Password;

public class RegisterDialog extends DialogFragment {
    DialogRegisterBinding binding;

    public void switchToLoginDialog() {
        new LoginDialog().show(getParentFragmentManager(), null);
        dismiss();
    }

    public void confirmRegister() {
        // get user inputs from the fields
        String uidValue = binding.etLoginUid.getEditableText().toString();
        String pwdValue = binding.etLoginPwd.getEditableText().toString();
        String pwdValueConfirmed = binding.etLoginPwdConfirmed.getEditableText().toString();

        if (uidValue.length() < 12 || uidValue.length() > 20) {
            binding.etLoginUid.setError("length should be between 12 to 20 characters.");
            return;
        }

        if (!pwdValue.equals(pwdValueConfirmed)) {
            binding.etLoginPwdConfirmed.setError("Unmatched password.");
            return;
        }

        if (Password.validate(pwdValue) != null) {
            binding.etLoginPwd.setError(Password.validate(pwdValue));
            return;
        }

        new AccountRequest().register(uidValue, Password.hash(pwdValue), res -> {

            if (res != null && res.optInt("responseCode") == 200) {
                try {
                    String date = (String) ((JSONObject) res.get("response")).get("createdDate");

                    Bundle bundle = new Bundle();
                    bundle.putString("uid", uidValue);
                    bundle.putString("desc", date.substring(0, 10));

                    getParentFragmentManager().setFragmentResult("accountStatus", bundle);
                    dismiss();
                } catch (Exception e) {
                    System.out.println(e);
                }
                return;
            }

            System.out.println("Failed");
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_register, container, false);
        binding.setFragment(this);
        binding.setTheme(((MainActivity) requireActivity()).theme);

        requireDialog().getWindow().setStatusBarColor(binding.getTheme().secondary);
        requireDialog().getWindow().setNavigationBarColor(binding.getTheme().secondary);

        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
}
