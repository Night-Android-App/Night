package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "ringtone", foreignKeys = {
        @ForeignKey(
                entity = Product.class,
                parentColumns = "prod_id",
                childColumns = "prod_id",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        )
})
public class Ringtone {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "ring_name")
    public String name = "Default Ringtone";

    @NonNull
    @ColumnInfo(name = "prod_id")
    public Integer prodId;

    @NonNull
    public String path = "file://android_asset/ringtones/default_ringtone.mp3";
}
