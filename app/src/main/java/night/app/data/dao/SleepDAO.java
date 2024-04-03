package night.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import night.app.data.entities.Sleep;

@Dao
public interface SleepDAO {
    @Query("SELECT * FROM Sleeps LIMIT 1")
    Sleep getSleep();

    @Query("UPDATE Sleeps SET startTime=:startTime, endTime=:endTime, enableMission=:enableMission, enableDND=:enableDND, ringtoneId=:prodId")
    void updateSleep(int startTime, int endTime, int enableMission, int enableDND, Integer prodId);
    @Query("INSERT INTO Sleeps (startTime, endTime, enableMission, enableDND, ringtoneId) VALUES (:startTime, :endTime, :enableMission, :enableDND, :prodId)")
    void addSleep(int startTime, int endTime, int enableMission, int enableDND, Integer prodId);
}
