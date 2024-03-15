package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarm")
public class Alarm {

    @PrimaryKey
    @ColumnInfo(name = "alarm_id")
    public Integer id;

    @ColumnInfo(name = "start_time")
    public Integer startTime;

    @NonNull
    @ColumnInfo(name = "end_time")
    public Integer endTime;

    @NonNull
    @ColumnInfo(name = "ring_name")
    public String ringName;

    public Integer isAlarmEnabled;

    public Integer isDNDEnabled;

    public String repeat;
}
