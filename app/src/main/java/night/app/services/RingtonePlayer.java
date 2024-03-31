package night.app.services;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import night.app.activities.MainActivity;
import night.app.data.Ringtone;

public class RingtonePlayer {
    private MediaPlayer mediaPlayer;
    public Integer playerOwner;
    private Context context;
    private Integer id;

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void run() {
        new Thread(() -> {
            String path;
            if (id != null) {
                List<Ringtone> list = MainActivity.getDatabase().dao().getRingtone(id);
                if (list.size() > 0) {
                    path = list.get(0).path;
                }
                else {
                    path = "content://settings/system/alarm_alert";
                }
            }
            else {
                path = "content://settings/system/alarm_alert";
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    if (mediaPlayer != null) mediaPlayer.release();

                    mediaPlayer =  new MediaPlayer();
                    if (path.startsWith("file://android_asset/")) {
                        AssetFileDescriptor afd =
                                context.getAssets().openFd(path.replace("file://android_asset/", ""));
                        mediaPlayer.setLooping(true);

                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        afd.close();
                    }
                    else {
                        mediaPlayer.setLooping(true);
                        mediaPlayer.setDataSource(context, Uri.parse(path));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                }
                catch (Exception e) {
                    System.err.println(e);
                    System.err.println("???");
                }
            });
        }).start();
    }

    public void replaceRingtone(int id, int playerOwner) {
        if (mediaPlayer != null) mediaPlayer.release();

        this.id = id;
        this.playerOwner = playerOwner;
    }

    public RingtonePlayer(Context context) {
        this.context = context;
    }
}
