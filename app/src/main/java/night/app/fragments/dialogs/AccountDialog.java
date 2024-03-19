package night.app.fragments.dialogs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import night.app.R;
import night.app.activities.InitialActivity;
import night.app.activities.MainActivity;
import night.app.data.DataStoreHelper;
import night.app.databinding.DialogAccountBinding;
import night.app.networks.AccountRequest;
import night.app.services.Password;

public class  AccountDialog extends DialogFragment {
    DialogAccountBinding binding;
    private void register() {
        // get user inputs from the fields
        String uidValue = binding.etLoginUid.getEditableText().toString();
        String pwdValue = binding.etLoginPwd.getEditableText().toString();
        String pwdValueConfirmed = binding.etLoginPwdConfirmed.getEditableText().toString();

        if (uidValue.length() < 8 || uidValue.length() > 12) {
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

        ConfirmDialog dialog = new ConfirmDialog("", "", null);
        dialog.showNow(getParentFragmentManager(), null);

        dialog.showLoading();

        new AccountRequest().register(uidValue, Password.hash(pwdValue), res -> {
            try {
                if (res != null && res.optInt("responseCode") == 200) {
                    String sessionId = res.getJSONObject("response").getString("sessionId");

                    MainActivity.getDataStore().update(DataStoreHelper.KEY_SESSION, sessionId);

                    Bundle bundle = new Bundle();
                    bundle.putString("uid", uidValue);

                    ZoneId zoneId = ZoneId.systemDefault();
                    ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.now(), zoneId);

                    bundle.putString("desc", DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(dateTime));
                    bundle.putBoolean("isOnClickLogout", true);

                    getParentFragmentManager().setFragmentResult("accountStatus", bundle);
                    dialog.dismiss();
                    dismiss();

                    if (requireActivity() instanceof InitialActivity) requireActivity().finish();
                    return;
                }

                String errorMsg = switch (res.optInt("responseCode")) {
                    case 409 -> "Username is existed.";
                    case 404 -> "Request path not found";
                    case 500 -> "Internal server error";
                    default  -> "Unexpected status code: " + res.getInt("responseCode");
                };
                requireActivity().runOnUiThread(() -> {
                    dialog.replaceContent("Sign up Error", errorMsg, null);
                    dialog.showMessage();
                });
            }
            catch (JSONException e) {
                requireActivity().runOnUiThread(() -> {
                    dialog.replaceContent("Sign up Error", "Unexpected error (JSONException)", null);
                    dialog.showMessage();
                });
            }
        });
    }

    private void login() {
        // get user inputs from the fields
        String uidValue = binding.etLoginUid.getEditableText().toString();
        String pwdValue = binding.etLoginPwd.getEditableText().toString();

        ConfirmDialog dialog = new ConfirmDialog("", "", null);
        dialog.showNow(getParentFragmentManager(), null);

        dialog.showLoading();

        new AccountRequest().login(uidValue, Password.hash(pwdValue), res -> {
            if (res != null && res.optInt("responseCode") == 200) {
                try {
                    MainActivity.getDataStore().update(DataStoreHelper.KEY_COINS, res.getJSONObject("response").getInt("coins"));

                    String sid = res.getJSONObject("response").getString("sessionId");
                    MainActivity.getDataStore().update(DataStoreHelper.KEY_SESSION, sid);


                    Bundle bundle = new Bundle();
                    bundle.putString("uid", uidValue);

                    String date = res.getJSONObject("response").getString("createdDate");
                    bundle.putString("desc", date.substring(0, 10));

                    bundle.putBoolean("isOnClickLogout", true);

                    getParentFragmentManager().setFragmentResult("accountStatus", bundle);
                    dialog.dismiss();
                    dismiss();

                    if (requireActivity() instanceof InitialActivity) requireActivity().finish();
                }
                catch (JSONException e) {
                    requireActivity().runOnUiThread(() -> {
                        dialog.replaceContent("Login Error", "Unexpected error (JSONException).", null);
                        dialog.showMessage();
                    });
                }
                return;
            }

            String errorMsg = switch (res.optInt("responseCode", -1)) {
                case 404 -> "Username or password are incorrect.";
                case 500 -> "Internal server error";
                default  -> "Unexpected status code: " + res.optInt("responseCode");
            };

            requireActivity().runOnUiThread(() -> {
                dialog.replaceContent("Login Error", errorMsg, null);
                dialog.showMessage();
            });
        });
    }

    private void handleOnClickNegButton(View view) {
        binding.setIsLoginPage(!binding.getIsLoginPage());

        // close virtual keyboard
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        // clear text of all fields & lose focus
        for (TextView layout : new TextView[] {binding.etLoginUid, binding.etLoginPwd, binding.etLoginPwdConfirmed}) {
            layout.setText(null);
            layout.clearFocus();
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

        binding.setTheme(MainActivity.getAppliedTheme());
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
