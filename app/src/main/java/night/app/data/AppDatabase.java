package night.app.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import night.app.data.dao.AlarmDAO;
import night.app.data.dao.AppDAO;
import night.app.data.dao.SleepDAO;

@Database(entities = {Theme.class, Product.class, Ringtone.class, Day.class, Alarm.class, ProductType.class, Repeat.class, Sleep.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDAO dao();

    public abstract AlarmDAO alarmDAO();

    public abstract SleepDAO sleepDAO();
}
