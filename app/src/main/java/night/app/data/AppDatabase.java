package night.app.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import night.app.data.dao.AlarmDAO;
import night.app.data.dao.AppDAO;
import night.app.data.dao.DayDAO;
import night.app.data.dao.SleepDAO;
import night.app.data.dao.SleepEventDAO;
import night.app.data.entities.Alarm;
import night.app.data.entities.Day;
import night.app.data.entities.Product;
import night.app.data.entities.ProductType;
import night.app.data.entities.Repeat;
import night.app.data.entities.Ringtone;
import night.app.data.entities.Sleep;
import night.app.data.entities.SleepEvent;
import night.app.data.entities.Theme;

@Database(
        entities = {
                Theme.class,
                Product.class,
                Ringtone.class,
                Day.class,
                Alarm.class,
                ProductType.class,
                Repeat.class,
                Sleep.class,
                SleepEvent.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDAO dao();

    public abstract AlarmDAO alarmDAO();

    public abstract SleepDAO sleepDAO();

    public abstract DayDAO dayDAO();

    public abstract SleepEventDAO sleepEventDAO();
}
