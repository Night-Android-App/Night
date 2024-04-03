package night.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import night.app.data.entities.Alarm;

@Dao
public interface AlarmDAO {
    @Query("SELECT * FROM Alarms")
    List<Alarm> getAllAlarms();

    @Query("UPDATE Alarms SET endTime=:endTime, enableMission=:enableMission, ringtoneId=:prodId WHERE id=:id")
    void updateAlarm(int id, int endTime, int enableMission, Integer prodId);

    @Query("SELECT * FROM Alarms WHERE id=:id LIMIT 1")
    Alarm getAlarm(int id);

    @Query("UPDATE Alarms SET enableAlarm=:isEnabled WHERE id=:id")
    void updateAlarmEnabled(int id, int isEnabled);

    @Query("INSERT INTO Alarms (endTime, enableMission, ringtoneId) VALUES (:time, :enableMission, :prodId)")
    void createAlarm(int time, int enableMission, Integer prodId);

    @Query("DELETE FROM Alarms")
    void deleteAllAlarms();

    @Query("DELETE FROM Alarms WHERE id IN (:id)")
    void deleteAlarm(List<Integer> id);

    @Query("SELECT * FROM Alarms ORDER BY id DESC LIMIT 1")
    Alarm getLastAlarm();
}
