package night.app.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import night.app.databinding.ActivitySleepBinding;
import night.app.services.RingtonePlayer;
import night.app.utils.TimeUtils;


public class SleepActivity extends AppCompatActivity {
    private ActivitySleepBinding binding;
    private final RingtonePlayer player = new RingtonePlayer(this);

    private void countdown(int seconds) {
        new Thread(() -> {
            int remain = seconds;
            while (remain >= 0) {
                try {
                    Thread.sleep(1000);

                    String text = "(" + TimeUtils.toHrMinSec(remain) + ")";
                    runOnUiThread(() -> binding.tvCount.setText(text));
                    remain--;
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            player.run();
            binding.tvCount.setText("End");
        }).start();
    }

    private void updateCurrentTime() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);

                runOnUiThread(() -> {
                    binding.tvCurrent.setText(TimeUtils.toTimeNotation(Calendar.getInstance()));
                });

                updateCurrentTime();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void setWeekOfDay() {
        String[] dayName = new String[] {
                "Sunday", "Monday",
                "Tuesday", "Wednesday",
                "Thursday", "Friday",
                "Saturday"
        };

        int dayNum = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        if (dayNum < dayName.length) {
            binding.tvWeekOfDay.setText(dayName[dayNum]);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) player.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySleepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setTheme(MainActivity.getAppliedTheme());

        getWindow().setStatusBarColor(Color.parseColor("#1D134A"));
        getWindow().setNavigationBarColor(Color.parseColor("#221752"));

        binding.btnPos.setOnClickListener(v -> finish());

        if (getIntent() != null && getIntent().getExtras() != null) {
            int sleepMinutes = getIntent().getExtras().getInt("sleepMinutes");

            if (sleepMinutes >= 0) {
                Calendar calendar = Calendar.getInstance();
                binding.tvCurrent.setText(TimeUtils.toTimeNotation(calendar));
                
                calendar.add(Calendar.MINUTE, sleepMinutes);
                binding.tvWake.setText(TimeUtils.toTimeNotation(calendar));


                updateCurrentTime();

                String text = "(" + TimeUtils.toHrMinSec(sleepMinutes*60) + ")";
                binding.tvCount.setText(text);
                countdown(sleepMinutes * 60-1);
            }

            setWeekOfDay();
        }
    }
}
