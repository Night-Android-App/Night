package night.app.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName="Repeats", foreignKeys = {
        @ForeignKey(
                entity = Alarm.class,
                parentColumns = "id",
                childColumns = "alarmId",
                onDelete = ForeignKey.CASCADE
        )
})
public class Repeat {
    @PrimaryKey
    public Integer id;
    public Integer alarmId;

    public Integer day1;
    public Integer day2;
    public Integer day3;
    public Integer day4;
    public Integer day5;
    public Integer day6;
    public Integer day7;
}
