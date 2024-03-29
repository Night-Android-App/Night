package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Ringtones", foreignKeys = {
        @ForeignKey(
                entity = Product.class,
                parentColumns = "prodId",
                childColumns = "prodId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        )
})
public class Ringtone {

    @NonNull
    @PrimaryKey
    public String name = "Default Ringtone";

    @NonNull
    public Integer prodId;

    @NonNull
    public String path = "file://android_asset/ringtones/default_ringtone.mp3";
}
