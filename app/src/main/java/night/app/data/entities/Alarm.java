package night.app.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Alarms")
public class Alarm {

    @PrimaryKey
    public Integer id;

    @NonNull
    public Integer endTime;

    public Integer ringtoneId;

    public Integer enableAlarm;

    public Integer enableMission;
}
