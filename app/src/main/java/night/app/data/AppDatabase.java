package night.app.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Theme.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ThemeDAO themeDAO();
}