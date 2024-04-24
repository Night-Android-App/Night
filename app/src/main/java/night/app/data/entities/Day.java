package night.app.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Days")
public class Day {
    @NonNull
    @PrimaryKey
    public Integer date;

    public Integer startTime;

    public Integer endTime;

    public String dream;
}
