package night.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import night.app.data.Sleep;

@Dao
public interface SleepDAO {
    @Query("SELECT * FROM Sleeps WHERE day=:day LIMIT 1")
    Sleep getSleep(int day);

    @Query("UPDATE Sleeps SET startTime=:startTime, endTime=:endTime, enableMission=:enableMission, enableDND=:enableDND, ringtoneId=:prodId")
    void updateSleep(int startTime, int endTime, int enableMission, int enableDND, int prodId);
}
