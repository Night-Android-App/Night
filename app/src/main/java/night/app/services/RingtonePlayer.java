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
        mediaPlayer.release();
    }

    public void run() {
        new Thread(() -> {
            try {
                String path = activity.appDatabase.dao().getRingtone(ringtoneName).get(0).path;

                mediaPlayer = new MediaPlayer();

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
                System.err.println(e.toString());
            }
        }).start();
    }

    public RingtonePlayer(MainActivity activity, String ringtoneName, int playerOwner) {
        this.activity = activity;
        this.ringtoneName = ringtoneName;
        this.playerOwner = playerOwner;
    }
}
