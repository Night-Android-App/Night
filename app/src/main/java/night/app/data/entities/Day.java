package night.app.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Days")
public class Day {
    @NonNull
    @PrimaryKey
    public Long date;

    public Long startTime;

    public Long endTime;

    public String dream;
}
