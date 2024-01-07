package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.networks.AccountRequest;
import night.app.services.Password;

public class LoginDialog extends BottomSheetDialogFragment {
    private void handleOnClickLogin(View view) {
        TextInputEditText etLoginUID = view.findViewById(R.id.et_login_uid);
        String uidValue = etLoginUID.getEditableText().toString();

        TextInputEditText etLoginPwd = view.findViewById(R.id.et_login_pwd);
        String pwdValue = etLoginPwd.getEditableText().toString();

        // async
        new Thread(() -> {
            try {
                JSONObject response = AccountRequest.login(uidValue, Password.hash(pwdValue));

                // login success
                if (response.getInt("status") == 200) {
                    ((MainActivity) requireActivity())
                            .dataStore.update("sessionID", response.getString("sessionID"));

                    System.out.println("Success");
                }
                else {
                    System.out.println("Failed");
                }
            }
            catch (Exception e) {
                System.out.println("Failed");
            }
        }).start();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_login, null);

        view.findViewById(R.id.btn_login).setOnClickListener(v -> handleOnClickLogin(view));

        return view;
    }
}
