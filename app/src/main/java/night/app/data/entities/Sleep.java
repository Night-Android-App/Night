package night.app.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="Sleeps")
public class Sleep {
    @PrimaryKey
    public Integer day;

    @NonNull
    public Long startTime;

    @NonNull
    public Long endTime;

    public Integer enableMission;

    public Integer enableDND;

    public Integer ringtoneId;
}

