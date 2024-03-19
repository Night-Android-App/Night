package night.app.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.PreferencesKeys;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import night.app.databinding.ActivityInitialBinding;
import night.app.fragments.dialogs.AccountDialog;

public class InitialActivity extends AppCompatActivity {
    private ActivityInitialBinding binding;

    private interface Handler {
        void run(View view);
    }

    private ClickableSpan getHyperLinkSpan(Handler handler) {
        return new ClickableSpan() {
            @Override
            public void onClick(View view) {
                handler.run(view);
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                super.updateDrawState(textPaint);
                textPaint.setUnderlineText(true);
                textPaint.setColor(Color.BLUE);
            }
        };
    }

    private void setOnBackPressedListener() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                System.exit(0);
            }
        });
    }

    private void handleOnClickLogin() {
        new AccountDialog().show(getSupportFragmentManager(), null);
    }

    private void handleOncClickContinue() {
        MainActivity.getDataStore().update(PreferencesKeys.booleanKey("isServiceStarted"), true);
        finish();
    }

    private void setAgreementInfoTextStyle() {
        SpannableString spanString = new SpannableString(binding.tvAgreements.getText());

        spanString.setSpan(getHyperLinkSpan(v -> {
            Intent intent = new Intent(this, AgreementActivity.class);
            intent.putExtra("type", AgreementActivity.TYPE_TERMS);

            startActivity(intent);
        }), 42, 54, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spanString.setSpan(getHyperLinkSpan(v -> {
            Intent intent = new Intent(this, AgreementActivity.class);
            intent.putExtra("type", AgreementActivity.TYPE_POLICY);

            startActivity(intent);
        }), 59, 73, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.tvAgreements.setText(spanString);
        binding.tvAgreements.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvAgreements.setHighlightColor(Color.TRANSPARENT);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInitialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setOnBackPressedListener();

        binding.tvContinue.setOnClickListener(v -> handleOncClickContinue());
        binding.tvContinue.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        binding.btnLogin.setOnClickListener(v -> handleOnClickLogin());

        setAgreementInfoTextStyle();

        getWindow().setStatusBarColor(MainActivity.getAppliedTheme().getSurface());
        getWindow().setNavigationBarColor(MainActivity.getAppliedTheme().getSurface());
    }
}