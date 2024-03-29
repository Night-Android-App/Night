package night.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import java.util.List;

import night.app.R;
import night.app.adapters.RingtoneAdapter;
import night.app.adapters.RingtoneOwnedAdapter;
import night.app.data.Ringtone;
import night.app.databinding.ActivityRingtoneBinding;

public class RingtoneActivity extends AppCompatActivity {
    private ActivityRingtoneBinding binding;

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
    }
}