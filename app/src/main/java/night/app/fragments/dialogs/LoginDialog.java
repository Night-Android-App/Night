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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.data.PreferenceViewModel;
import night.app.databinding.DialogLoginBinding;
import night.app.fragments.SettingsPageFragment;
import night.app.networks.AccountRequest;
import night.app.services.Password;

public class LoginDialog extends BottomSheetDialogFragment {
    DialogLoginBinding binding;
    public void switchToRegisterDialog() {
        dismiss();
        new RegisterDialog().show(getParentFragmentManager(), null);
    }

    private void initFieldOnKeyListener(View view) {
        LinearLayout fieldContainer = binding.llFormFields;

        // loop all fields and set key down listener
        for (int i = 0; i < fieldContainer.getChildCount(); i++) {
            EditText etFieldChild = ((TextInputLayout) fieldContainer.getChildAt(i))
                    .getEditText();


            if (etFieldChild == null) continue;

            // for last field, enter key down will close keyboard & submit action
            if (i == fieldContainer.getChildCount()-1) {
                etFieldChild.setOnKeyListener((etView, code, event) -> {
                    if (event.getAction() != KeyEvent.ACTION_DOWN || code != KeyEvent.KEYCODE_ENTER)
                        return false;

                    // hide virtual keyboard
                    ((InputMethodManager) requireActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(view.getWindowToken(), 0);

                    etView.clearFocus();
                    return true;
                });
                break;
            }

            // for other fields, enter key down will changes the focus to the next field

            // lambda expression accept only final outer variable
            // effectively final, no value assignment later on
            int nextChildIdx = i + 1;

            etFieldChild.setOnKeyListener((etView, code, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && code == KeyEvent.KEYCODE_ENTER) {
                    EditText etNextFieldChild = ((TextInputLayout) fieldContainer
                            .getChildAt(nextChildIdx))
                            .getEditText();

                    if (etNextFieldChild == null) return false;

                    etNextFieldChild.requestFocus();
                    return true;
                }
                return false;
            });
        }
    }
    public void handleOnClickLogin(View view) {
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

        View view = binding.getRoot();

        binding.etLoginUid.requestFocus();

        initFieldOnKeyListener(view);

        return view;
    }
}
