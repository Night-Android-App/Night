package night.app.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import night.app.databinding.ActivityAgreementBinding;

public class AgreementActivity extends AppCompatActivity {
    private ActivityAgreementBinding binding;

    public final static int TYPE_POLICY = 0;
    public final static int TYPE_TERMS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAgreementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int type = bundle.getInt("type", 0);
            if (type == TYPE_POLICY) {
                binding.wvDoc.loadUrl("file:///android_asset/privacy_policy.html");
                binding.btnPos.setOnClickListener(v -> finish());
            }
            else if (type == TYPE_TERMS) {
                binding.wvDoc.loadUrl("file:///android_asset/terms_of_use.html");
                binding.btnPos.setOnClickListener(v -> finish());
            }
        }

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
    }
}
