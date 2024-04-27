package night.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import night.app.data.entities.Sleep;

@Dao
public interface SleepDAO {
    @Query("SELECT * FROM Sleeps")
    Sleep getSleep();

    @Query("REPLACE INTO Sleeps (day, startTime, endTime, enableMission, enableDND, ringtoneId) " +
            "VALUES (1, :startTime, :endTime, :enableMission, :enableDND, :prodId)")
    void insertUpdate(long startTime, long endTime, int enableMission, int enableDND, Integer prodId);
}
