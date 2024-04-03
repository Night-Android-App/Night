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

    @NonNull
    public String sleep;

    public String dream;
}
