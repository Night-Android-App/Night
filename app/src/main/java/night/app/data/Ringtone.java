package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ringtone")
public class Ringtone {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "ring_name")
    public String name = "Default Ringtone";

    @NonNull
    public Integer duration = 23;

    @NonNull
    public String path = "file://android_asset/ringtones/default_ringtone.mp3";
}
