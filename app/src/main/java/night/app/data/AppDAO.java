package night.app.data;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppDAO {
    // Alarm Query
    @Query("SELECT * FROM alarm WHERE alarm_id == 1")
    List<Alarm> getSleepAlarm();

    @Query("UPDATE alarm SET start_time=:startTime, end_time=:endTime, ring_name=:ringName, isAlarmEnabled=:isAlarmEnabled, isDNDEnabled=:isDNDEnabled")
    void updateAlarm(int startTime, int endTime, String ringName, int isAlarmEnabled, int isDNDEnabled);

    @Query("SELECT * FROM alarm WHERE alarm_id != 1")
    List<Alarm> getAlarms();

    @Query("SELECT * FROM alarm WHERE alarm_id=:id LIMIT 1")
    List<Alarm> getAlarmSettings(int id);

    @Query("INSERT INTO alarm (end_time, isAlarmEnabled, isDNDEnabled, ring_name) VALUES (:time, :isAlarmEnabled, :isDNDEnabled, :ringName)")
    void createAlarm(int time, int isAlarmEnabled, int isDNDEnabled, String ringName);

    @Query("DELETE FROM alarm WHERE alarm_id=:id")
    void deleteAlarm(int id);

    // Analysis Query
    @Query("SELECT * FROM day WHERE day_id=:id LIMIT 1")
    List<Day> getDayByID(int id);

    @Query("SELECT * FROM day WHERE date=:date LIMIT 1")
    List<Day> getDayByDate(int date);

    @Query("SELECT * FROM day WHERE date BETWEEN :start AND :end")
    List<Day> getDayRange(int start, int end);

    // Shop Query
    @Query("SELECT * FROM product WHERE prod_type=:prodType")
    List<Product> getProducts(String prodType);

    @Query("SELECT * FROM theme WHERE theme_name=:themeName LIMIT 1")
    List<Theme> getTheme(String themeName);

    @Query("SELECT * FROM ringtone WHERE ring_name=:ringName LIMIT 1")
    List<Ringtone> getRingtone(String ringName);

    @Query("SELECT * FROM ringtone WHERE ring_name in (SELECT ring_name FROM product WHERE prod_isbought=1)")
    List<Ringtone> getAllOwnedRingtones();

    @Query("INSERT INTO ringtone (ring_name, file) VALUES (:ringName, :file)")
    void insertRingtone(String ringName, Byte file);

    @Query("INSERT INTO theme (theme_name, `primary`, secondary, surface, surface_variant, accent, onPrimary, onPrimary_variant, onSurface) VALUES (:id, :pr, :sec, :sur, :surVar, :accent, :onPr, :onPrVar, :onSur)")
    void insertTheme(String id, int pr, int sec, int sur, int surVar, int accent, int onPr, int onPrVar, int onSur);
}
