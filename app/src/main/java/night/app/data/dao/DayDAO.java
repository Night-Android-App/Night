package night.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import night.app.data.entities.Day;

@Dao
public interface DayDAO {
    @Query("SELECT * FROM Days WHERE date=:date LIMIT 1")
    Day getDayByDate(long date);

    @Query("SELECT * FROM Days ORDER BY date DESC LIMIT 1")
    List<Day> getRecentDay();

    @Query("SELECT * FROM Days WHERE date BETWEEN :start AND :end")
    List<Day> getDayRange(long start, long end);

    @Query("SELECT * FROM Days ORDER BY date DESC LIMIT 30")
    List<Day> getAllDay();

    @Query("DELETE FROM Days WHERE date < :dayBefore")
    void deleteOldDays(long dayBefore);

    @Query("REPLACE INTO Days (date, startTime, endTime, dream) " +
            "VALUES (:date, :startTime, :endTime, :dream)")
    void create(long date, long startTime, long endTime, String dream);

    @Query("DELETE FROM Days")
    void deleteAllDays();

}
