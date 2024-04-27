package night.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import night.app.data.entities.Day;

@Dao
public interface DayDAO {
    @Query("REPLACE INTO Days (date, startTime, endTime) VALUES (:date, :startTime, :endTime)")
    void create(long date, long startTime, long endTime);

    @Query("UPDATE Days SET dream=:dream WHERE date=:date")
    void updateDream(long date, String dream);

    @Query("SELECT * FROM Days ORDER BY date DESC LIMIT 30")
    List<Day> getAll();

    @Query("SELECT * FROM Days ORDER BY date DESC LIMIT 1")
    List<Day> getRecent();

    @Query("SELECT * FROM Days WHERE date=:date LIMIT 1")
    Day getByDate(long date);

    @Query("SELECT * FROM Days WHERE date BETWEEN :start AND :end")
    Day[] getByRange(long start, long end);

    @Query("DELETE FROM Days")
    void deleteAll();

    @Query("DELETE FROM Days WHERE date < :dayBefore")
    void deleteOldDays(long dayBefore);
}
