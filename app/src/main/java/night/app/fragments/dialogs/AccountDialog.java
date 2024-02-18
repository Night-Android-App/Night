package night.app.fragments.dialogs;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.databinding.DialogAccountBinding;
import night.app.networks.AccountRequest;
import night.app.services.Password;

public class AccountDialog extends DialogFragment {
    DialogAccountBinding binding;
    private void register() {
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

    private void login() {
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

    private void handleOnClickNegButton(View view) {
        binding.setIsLoginPage(!binding.getIsLoginPage());

        for (TextView layout : new TextView[] {binding.etLoginUid, binding.etLoginPwd, binding.etLoginPwdConfirmed}) {
            layout.setText(null);
        }

    }

    private void handleOnClickPosButton(View view) {
        if (binding.getIsLoginPage()) {
            login();
            return;
        }
        register();
    }

    private void loadTextInputLayoutStyle() {
        for (TextInputLayout layout : new TextInputLayout[] {binding.tilUid, binding.tilPwd, binding.tilPwdConfirm}) {
            layout.setDefaultHintTextColor(ColorStateList.valueOf(binding.getTheme().getOnSurface()));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_account, container, false);

        binding.setTheme(((MainActivity) requireActivity()).theme);
        requireDialog().getWindow().setStatusBarColor(binding.getTheme().getSecondary());
        requireDialog().getWindow().setNavigationBarColor(binding.getTheme().getSecondary());

        loadTextInputLayoutStyle();

        binding.setIsLoginPage(true);
        binding.btnNegative.setOnClickListener(this::handleOnClickNegButton);
        binding.btnPositive.setOnClickListener(this::handleOnClickPosButton);

        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
}
