package night.app.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import night.app.data.entities.Alarm;

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

    public Integer day1 = 0;
    public Integer day2 = 0;
    public Integer day3 = 0;
    public Integer day4 = 0;
    public Integer day5 = 0;
    public Integer day6 = 0;
    public Integer day7 = 0;
}
