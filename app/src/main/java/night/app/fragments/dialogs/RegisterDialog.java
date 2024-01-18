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
import night.app.databinding.DialogRegisterBinding;
import night.app.fragments.SettingsPageFragment;
import night.app.networks.AccountRequest;
import night.app.services.Password;

public class RegisterDialog extends BottomSheetDialogFragment {
    DialogRegisterBinding binding;

    public void switchToLoginDialog() {
        dismiss();
        new LoginDialog().show(getParentFragmentManager(), null);
    }

    public void confirmRegister(View view) {
        // get user inputs from the fields
        String uidValue = binding.etLoginUid.getEditableText().toString();
        String pwdValue = binding.etLoginPwd.getEditableText().toString();

//        new AccountRequest().login(uidValue, Password.hash(pwdValue), res -> {
//
//            if (res != null && res.optInt("responseCode") == 200) {
//                try {
//                    String date = (String) ((JSONObject) res.get("response")).get("createdDate");
//
//                    Bundle bundle = new Bundle();
//                    bundle.putString("uid", uidValue);
//                    bundle.putString("desc", date.substring(0, 10));
//
//                    getParentFragmentManager().setFragmentResult("accountStatus", bundle);
//                    dismiss();
//                } catch (Exception e) {
//                    System.out.println(e);
//                }
//                return;
//            }
//
//            System.out.println("Failed");
//            // code
//        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_register, container, false);
        binding.setFragment(this);

        return binding.getRoot();
    }
}
