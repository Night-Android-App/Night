package night.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Product {

    @PrimaryKey
    @ColumnInfo(name = "prod_id")
    public Integer prodId;

    @NonNull
    @ColumnInfo(name = "prod_name")
    public String prodName;

    @NonNull
    @ColumnInfo(name = "prod_type")
    public String prodType;

    @NonNull
    @ColumnInfo(name = "prod_price")
    public Integer prodPrice;

    @ColumnInfo(name = "prod_isbought")
    public Integer prodIsBought;
}
