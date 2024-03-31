package night.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import night.app.R;
import night.app.adapters.RingtoneAdapter;
import night.app.adapters.RingtoneOwnedAdapter;
import night.app.data.Ringtone;
import night.app.databinding.ActivityRingtoneBinding;
import night.app.utils.LayoutUtils;

public class RingtoneActivity extends AppCompatActivity {
    private ActivityRingtoneBinding binding;

    private Integer ringtoneId = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtoneId != null) {
            Intent intent = new Intent();
            intent.putExtra("ringtoneId", ringtoneId);
            setResult(RESULT_OK, intent);
        }
    }

    public void selectRingtone(int id) {
        ringtoneId = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRingtoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setTheme(MainActivity.getAppliedTheme());

        binding.rvRingtones.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            List<Ringtone> ringtones = MainActivity.getDatabase().dao().getAllOwnedRingtones();

            runOnUiThread(() -> {
                binding.rvRingtones.setAdapter(new RingtoneOwnedAdapter(this, ringtones));
            });
        }).start();

        LayoutUtils.setSystemBarColor(getWindow(), binding.getTheme().getPrimary(), binding.getTheme().getSecondary());

        binding.textView7.setOnClickListener(v -> finish());
    }
}