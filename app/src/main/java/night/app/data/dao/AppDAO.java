package night.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import night.app.data.Day;
import night.app.data.Product;
import night.app.data.Ringtone;
import night.app.data.Theme;

@Dao
public interface AppDAO {
    // Analysis Query
    @Query("SELECT * FROM Days WHERE date=:date LIMIT 1")
    List<Day> getDayByDate(long date);

    @Query("SELECT * FROM Days ORDER BY date DESC LIMIT 1")
    List<Day> getRecentDay();

    @Query("SELECT * FROM Days WHERE date BETWEEN :start AND :end")
    List<Day> getDayRange(long start, long end);

    @Query("SELECT * FROM Days ORDER BY date DESC LIMIT 30")
    List<Day> getAllDay();

    @Query("DELETE FROM Days WHERE date < :dayBefore")
    void deleteOldDays(long dayBefore);

    @Query("INSERT INTO Days (date, sleep, dream) VALUES (:date, :sleep, :dream)")
    void insertDay(int date, String sleep, String dream);

    @Query("DELETE FROM Days")
    void deleteAllDays();

    // Shop Query
    @Query("SELECT * FROM Products WHERE type=:type")
    List<Product> getProducts(Integer type);

    @Query("SELECT * FROM Themes WHERE prodId=:id LIMIT 1")
    List<Theme> getTheme(int id);

    @Query("SELECT * FROM Ringtones WHERE prodId=:id LIMIT 1")
    List<Ringtone> getRingtone(int id);

    @Query("SELECT * FROM Ringtones WHERE prodId in (SELECT prodId FROM Products WHERE isBought=1)")
    List<Ringtone> getAllOwnedRingtones();

    @Query("UPDATE Products SET isBought=1 WHERE prodId=:id ")
    void updateProductStatus(int id);
}
