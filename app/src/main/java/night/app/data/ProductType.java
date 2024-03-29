package night.app.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="ProductTypes")
public class ProductType {
    @PrimaryKey(autoGenerate = true)
    Integer id;

    @NonNull
    String type;
}
