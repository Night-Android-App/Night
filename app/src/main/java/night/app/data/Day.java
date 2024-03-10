package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Day {
    @NonNull
    @PrimaryKey
    public Integer date;

    @NonNull
    public String sleep;

    public String dream;
}
