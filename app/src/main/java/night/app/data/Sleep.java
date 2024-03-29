package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="Sleeps")
public class Sleep {
    @PrimaryKey
    public Integer day;

    @NonNull
    public Integer startTime;

    @NonNull
    public Integer endTime;

    public Integer enableMission;

    public Integer enableDND;

    public Integer ringtoneId;
}

