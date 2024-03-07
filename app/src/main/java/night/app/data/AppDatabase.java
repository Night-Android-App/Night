package night.app.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Theme.class, Product.class, Ringtone.class, Day.class, Alarm.class, ProductType.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDAO dao();
}
