package night.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import night.app.data.entities.SleepEvent;

@Dao
public interface SleepEventDAO {
    @Query("INSERT INTO SleepEvents (timeline, confidence, light, motion) " +
            "VALUES (:timestamp, :confidence, :light, :motion)")
    void create(long timestamp, int confidence, int light, int motion);

    @Query("SELECT * FROM SleepEvents " +
            "WHERE timeline >= :startTimeInSeconds AND timeline < (:startTimeInSeconds + 24*60*60)")
    SleepEvent[] getEventsByDay(long startTimeInSeconds);

    @Query("SELECT * FROM SleepEvents " +
            "WHERE timeline BETWEEN :date + :startInMills AND :date + :endInMills")
    SleepEvent[] getByRange(Long date, Long startInMills, Long endInMills);
}