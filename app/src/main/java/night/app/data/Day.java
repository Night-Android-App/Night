package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Day {
    @PrimaryKey
    @ColumnInfo(name = "day_id")
    public Integer id;

    @NonNull
    public Integer date;

    public String sleep;

    public String dream;
}
