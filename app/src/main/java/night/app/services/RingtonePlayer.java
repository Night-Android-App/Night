package night.app.services;

import android.app.Activity;
import android.media.MediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import night.app.activities.MainActivity;

public class RingtonePlayer {
    private MediaPlayer mediaPlayer;
    private MainActivity activity;
    private String ringtoneName;

    public void release() {
        mediaPlayer.release();
    }

    public void run() {
        new Thread(() -> {
            try {
                String path = activity.getCacheDir() + "/" + ringtoneName + ".mp3";

                File file = new File(path);
                FileOutputStream out = new FileOutputStream(file);

                String base64 = activity.appDatabase.dao().getRingtone(ringtoneName).get(0).file;
                byte[] bytes = Base64.getDecoder().decode(base64);
                out.write(bytes);

                out.close();

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(path);

                mediaPlayer.prepare();
                mediaPlayer.start();
            }
            catch (Exception e) {
                System.err.println(e);
            }

        }).start();
    }

    public RingtonePlayer(MainActivity activity, String ringtoneName) {
        this.activity = activity;
        this.ringtoneName = ringtoneName;
    }
}
