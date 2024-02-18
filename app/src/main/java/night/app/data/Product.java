package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"prod_name", "type"}, foreignKeys = {
        @ForeignKey(
                entity = Theme.class,
                parentColumns = "theme_name",
                childColumns = "prod_name",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Ringtone.class,
                parentColumns = "ring_name",
                childColumns = "prod_name",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        )
})
public class Product {
    @NonNull
    public String type;

    @NonNull
    @ColumnInfo(name = "prod_name")
    public String prodName;

    @NonNull
    public Integer price;

    public Integer isBought;

    public Product(@NonNull String type, @NonNull String prodName, @NonNull Integer price, Integer isBought) {
        this.type = type;
        this.prodName = prodName;
        this.price = price;
        this.isBought = isBought;
    }
}
