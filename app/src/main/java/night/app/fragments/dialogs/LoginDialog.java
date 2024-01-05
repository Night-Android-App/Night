package night.app.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import night.app.R;
import night.app.networks.AccountRequest;
import night.app.services.Password;

public class LoginDialog extends DialogFragment {

    private void handleOnClickLogin(View view) {
        String uid = ((TextInputEditText) view.findViewById(R.id.et_login_uid)).getEditableText().toString();

        String pwd = ((TextInputEditText) view.findViewById(R.id.et_login_pwd)).getEditableText().toString();

        new Thread(() -> {
            try {
                AccountRequest.login(uid, Password.hash(pwd));
            }
            catch (Exception e) {
                System.out.print(e);
            }
        }).start();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(view);

        view.findViewById(R.id.btn_login).setOnClickListener(v -> handleOnClickLogin(view));

        return builder.create();
    }
}
