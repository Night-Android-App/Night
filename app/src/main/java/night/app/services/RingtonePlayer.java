package night.app.services;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import night.app.activities.MainActivity;

public class RingtonePlayer {
    private MediaPlayer mediaPlayer;
    public int playerOwner;
    private MainActivity activity;
    private String ringtoneName;

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public void run() {
        new Thread(() -> {
            String path = activity.appDatabase.dao().getRingtone(ringtoneName).get(0).path;
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
                }
                catch (Exception e) {
                    System.err.println(e);
                }
            });
        }).start();
    }

    public void replaceRingtone(String ringtoneName, int playerOwner) {
        if (mediaPlayer != null) mediaPlayer.release();

        this.ringtoneName = ringtoneName;
        this.playerOwner = playerOwner;
    }

    public RingtonePlayer(MainActivity activity) {
        this.activity = activity;
    }
}
