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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.networks.AccountRequest;
import night.app.services.Password;

public class LoginDialog extends BottomSheetDialogFragment {
    private void initFieldOnKeyListener(View view) {
        LinearLayout fieldContainer = view.findViewById(R.id.ll_form_fields);

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
    private void handleOnClickLogin(View view) {
        // get user inputs from the fields
        String uidValue = ((TextInputEditText) view.findViewById(R.id.et_login_uid))
                .getEditableText()
                .toString();

        String pwdValue = ((TextInputEditText) view.findViewById(R.id.et_login_pwd))
                .getEditableText()
                .toString();

        new AccountRequest().login(uidValue, Password.hash(pwdValue), res -> {

            if (res != null && res.optInt("responseCode") == 200) {
                System.out.println("Success");
                // code
                return;
            }

            System.out.println("Failed");
            // code
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_login, container);

        view.findViewById(R.id.et_login_uid).requestFocus();

        initFieldOnKeyListener(view);
        view.findViewById(R.id.btn_login).setOnClickListener(v -> handleOnClickLogin(view));

        return view;
    }
}
