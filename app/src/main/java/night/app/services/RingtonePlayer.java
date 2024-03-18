package night.app.services;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import night.app.activities.MainActivity;

public class RingtonePlayer {
    private MediaPlayer mediaPlayer;
    public Integer playerOwner;
    private AppCompatActivity activity;
    private Integer id;

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public void run() {
        new Thread(() -> {
            String path;
            if (id != null) {
                path = MainActivity.getDatabase().dao().getRingtone(id).get(0).path;
            }
            else {
                path = "content://settings/system/alarm_alert";
            }
            activity.runOnUiThread(() -> {
                try {
                    if (mediaPlayer != null) mediaPlayer.release();

                    mediaPlayer =  new MediaPlayer();
                    if (path.startsWith("file://android_asset/")) {
                        AssetFileDescriptor afd =
                                activity.getAssets().openFd(path.replace("file://android_asset/", ""));

                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        afd.close();
                    }
                    else {
                        mediaPlayer.setDataSource(activity, Uri.parse(path));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                }
                catch (Exception e) {
                    System.err.println(e);
                }
            });
        }).start();
    }

    public void replaceRingtone(int id, int playerOwner) {
        if (mediaPlayer != null) mediaPlayer.release();

        this.id = id;
        this.playerOwner = playerOwner;
    }

    public RingtonePlayer(AppCompatActivity activity) {
        this.activity = activity;
    }
}
