package night.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import night.app.data.entities.Day;

@Dao
public interface DayDAO {
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

}
