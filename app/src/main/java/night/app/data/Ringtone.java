package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Ringtone {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "ring_name")
    public String name;

    @NonNull
    public byte[] file;
}
