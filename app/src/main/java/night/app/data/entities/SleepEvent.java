package night.app.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SleepEvents")
public class SleepEvent {
    @PrimaryKey @NonNull
    public long timeline;

    @NonNull
    public int confidence;

    @NonNull
    public int motion;

    @NonNull
    public int light;
}
