package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "product", foreignKeys = {
        @ForeignKey(
                entity = ProductType.class,
                parentColumns = "id",
                childColumns = "type",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        )
})
public class Product {
    @PrimaryKey
    @ColumnInfo(name = "prod_id")
    public Integer prodId = -1;

    @NonNull
    public Integer type;

    @NonNull
    public Integer price;

    public Integer isBought;

    public Product(@NonNull Integer type, @NonNull Integer price, Integer isBought) {
        this.type = type;
        this.price = price;
        this.isBought = isBought;
    }
}
