package com.night.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.night.app.R;
import com.night.app.networks.AccountRequest;
import com.night.app.services.Password;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class LoginModal extends DialogFragment {

    private void handleOnClickLogin(View view) {
        String uid = ((TextInputEditText) view.findViewById(R.id.uidField)).getEditableText().toString();

        String pwd = ((TextInputEditText) view.findViewById(R.id.pwdField)).getEditableText().toString();

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

        View view = inflater.inflate(R.layout.activity_login_modal, null);
        builder.setView(view);

        view.findViewById(R.id.loginButton).setOnClickListener(v -> handleOnClickLogin(view));

        return builder.create();
    }
}
