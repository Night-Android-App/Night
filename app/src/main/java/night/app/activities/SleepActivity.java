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
                    String text = "( " + remain + " )";
                    runOnUiThread(() -> binding.tvCount.setText(text));
                    remain--;
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            player.run();
        }).start();
    }

    private void updateCurrentTime() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Calendar calendar = Calendar.getInstance();

                runOnUiThread(() -> {
                    binding.tvCurrent.setText(
                            TimeUtils.toTimeNotation(
                                    calendar.get(Calendar.HOUR) * 60 * 60
                                            + (calendar.get(Calendar.AM_PM) == Calendar.PM ? 12 : 0) * 60 * 60
                                            + calendar.get(Calendar.MINUTE) * 60
                            )
                    );
                });
                updateCurrentTime();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
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

                binding.tvCurrent.setText(
                        TimeUtils.toTimeNotation(
                                calendar.get(Calendar.HOUR) * 60 * 60
                                        + (calendar.get(Calendar.AM_PM) == Calendar.PM ? 12 : 0) * 60 * 60
                                        + calendar.get(Calendar.MINUTE) * 60
                        )
                );
                
                calendar.add(Calendar.MINUTE, sleepMinutes);

                binding.tvWake.setText(
                        TimeUtils.toTimeNotation(
                                calendar.get(Calendar.HOUR) * 60 * 60
                                        + (calendar.get(Calendar.AM_PM) == Calendar.PM ? 12 : 0) * 60 * 60
                                        + calendar.get(Calendar.MINUTE) * 60
                        )
                );

                binding.tvCount.setText("( " + sleepMinutes*60 + " )");

                updateCurrentTime();
                countdown(sleepMinutes * 60);
            }
        }
    }
}
