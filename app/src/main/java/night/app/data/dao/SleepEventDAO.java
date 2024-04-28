package night.app.data.dao;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import night.app.data.entities.Day;
import night.app.data.entities.SleepEvent;

@Dao
public interface SleepEventDAO {
    @Query("INSERT INTO SleepEvents (timeline, confidence, light, motion) " +
            "VALUES (:timestamp, :confidence, :light, :motion)")
    void create(long timestamp, int confidence, int light, int motion);

    @Query("SELECT * FROM SleepEvents " +
            "WHERE timeline BETWEEN :date + :startInMills AND :date + :endInMills")
    SleepEvent[] get(Long date, Long startInMills, Long endInMills);

    @Transaction
    default SleepEvent[] get(@NonNull Day day) {
        return get(day.date, day.startTime, day.endTime);
    }

    @Query("DELETE FROM SleepEvents")
    void deleteAll();
}
