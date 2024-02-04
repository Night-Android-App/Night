package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Theme {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "theme_name")
    public String name;

    public Integer primary ;

    public Integer secondary;

    public Integer surface;

    @ColumnInfo(name="surface_variant")
    public Integer surfaceVariant;

    public Integer accent;

    public Integer onPrimary;

    @ColumnInfo(name = "onPrimary_variant")
    public Integer onPrimaryVariant;

    public Integer onSurface;
}
