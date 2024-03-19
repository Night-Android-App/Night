package night.app.data;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppDAO {
    // Alarm Query
    @Query("SELECT * FROM alarm")
    List<Alarm> getAllAlarms();

    @Query("UPDATE alarm SET start_time=:startTime, end_time=:endTime, ring_name=:ringName, isAlarmEnabled=:isAlarmEnabled, isDNDEnabled=:isDNDEnabled")
    void updateAlarm(int startTime, int endTime, String ringName, int isAlarmEnabled, int isDNDEnabled);

    @Query("SELECT * FROM alarm WHERE alarm_id=:id LIMIT 1")
    List<Alarm> getAlarmSettings(int id);

    @Query("UPDATE alarm SET isAlarmEnabled=:isEnabled WHERE alarm_id=:id")
    void updateAlarmEnabled(int id, int isEnabled);

    @Query("INSERT INTO alarm (end_time, isAlarmEnabled, isDNDEnabled, ring_name) VALUES (:time, :isAlarmEnabled, :isDNDEnabled, :ringName)")
    void createAlarm(int time, int isAlarmEnabled, int isDNDEnabled, String ringName);

    @Query("DELETE FROM alarm WHERE alarm_id=:id")
    void deleteAlarm(int id);

    @Query("DELETE FROM alarm")
    void deleteAllAlarms();

    @Query("DELETE FROM alarm WHERE alarm_id IN (:id)")
    void deleteAlarm(List<Integer> id);

    // Analysis Query
    @Query("SELECT * FROM day WHERE date=:date LIMIT 1")
    List<Day> getDayByDate(long date);

    @Query("SELECT * FROM day ORDER BY date DESC LIMIT 1")
    List<Day> getRecentDay();

    @Query("SELECT * FROM day WHERE date BETWEEN :start AND :end")
    List<Day> getDayRange(long start, long end);

    @Query("SELECT * FROM day ORDER BY date DESC LIMIT 30")
    List<Day> getAllDay();

    @Query("DELETE FROM day WHERE date < :dayBefore")
    void deleteOldDays(long dayBefore);

    @Query("INSERT INTO day (date, sleep, dream) VALUES (:date, :sleep, :dream)")
    void insertDay(int date, String sleep, String dream);

    @Query("DELETE FROM day")
    void deleteAllDays();

    // Shop Query
    @Query("SELECT * FROM product WHERE type=:type")
    List<Product> getProducts(Integer type);

    @Query("SELECT * FROM theme WHERE prod_id=:id LIMIT 1")
    List<Theme> getTheme(int id);

    @Query("SELECT * FROM ringtone WHERE prod_id=:id LIMIT 1")
    List<Ringtone> getRingtone(int id);

    @Query("SELECT * FROM ringtone WHERE ring_name in (SELECT ring_name FROM product WHERE isBought=1)")
    List<Ringtone> getAllOwnedRingtones();

    @Query("UPDATE product SET isBought=1 WHERE prod_id=:id ")
    void updateProductStatus(int id);

    @Query("INSERT INTO ringtone (ring_name, path) VALUES (:ringName, :path)")
    void insertRingtone(String ringName, String path);

    @Query("INSERT INTO theme (theme_name, `primary`, secondary, surface, accent, onPrimary, onSurface) VALUES (:id, :pr, :sec, :sur, :accent, :onPr, :onSur)")
    void insertTheme(String id, int pr, int sec, int sur,int accent, int onPr, int onSur);
}
