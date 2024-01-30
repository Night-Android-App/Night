package night.app.data;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Entity
public class Theme {

    @NonNull
    @PrimaryKey
    public String id;

    @ColorInt
    @ColumnInfo(name = "primary")
    public int primary;

    @ColorInt
    @ColumnInfo(name = "secondary")
    public int secondary;

    @ColorInt
    @ColumnInfo(name="onSecondary")
    public int onSecondary;

    @ColorInt
    @ColumnInfo(name="onSecondaryVariant")
    public int onSecondaryVariant;

    @ColorInt
    @ColumnInfo(name = "accent")
    public int accent;

    @ColorInt
    @ColumnInfo(name = "text")
    public int text;

    @ColorInt
    @ColumnInfo(name = "textInactive")
    public int textInactive;

    @ColorInt
    @ColumnInfo(name = "textContrast")
    public int textContrast;
}
