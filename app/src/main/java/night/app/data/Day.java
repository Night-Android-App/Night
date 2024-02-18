package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Day {
    @NonNull
    @PrimaryKey
    public Integer date = 0;

    @NonNull
    public String sleep = "{\"1422\": 0, \"10\": 52, \"60\": 90, \"120\": 86,  \"240\": 54, \"410\": 51,  \"420\": 0}";

    public String dream = "Lorem ipsum is placeholder text commonly used in the graphic, print, and publishing industries for previewing";
}
