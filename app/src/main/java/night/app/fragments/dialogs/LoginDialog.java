package night.app.fragments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.DialogLoginBinding;
import night.app.networks.AccountRequest;
import night.app.services.Password;

public class LoginDialog extends DialogFragment {
    DialogLoginBinding binding;

    public void switchToRegisterDialog() {
        dismiss();
        new RegisterDialog().show(getParentFragmentManager(), null);
    }

    public void handleOnClickLogin() {
        // get user inputs from the fields
        String uidValue = binding.etLoginUid.getEditableText().toString();
        String pwdValue = binding.etLoginPwd.getEditableText().toString();

        new AccountRequest().login(uidValue, Password.hash(pwdValue), res -> {

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
            // code
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_login, container, false);
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
