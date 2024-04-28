package night.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import night.app.data.entities.Alarm;

@Dao
public interface AlarmDAO {
    @Query("INSERT INTO Alarms (endTime, enableMission, ringtoneId) " +
            "VALUES (:time, :enableMission, :prodId)")
    void create(long time, int enableMission, Integer prodId);

    @Query("INSERT INTO Alarms (id, endTime, enableMission, enableAlarm, ringtoneId) " +
            "VALUES (:id, :time, :enableMission, :enableAlarm, :prodId)")
    void create(int id, long time, int enableMission, int enableAlarm, Integer prodId);

    @Query("UPDATE Alarms SET enableAlarm=:isEnabled WHERE id=:id")
    void setEnable(int id, int isEnabled);

    @Query("SELECT * FROM Alarms")
    List<Alarm> getAll();

    @Query("SELECT * FROM Alarms ORDER BY id DESC LIMIT 1")
    Alarm getLast();

    @Query("SELECT * FROM Alarms WHERE id=:id LIMIT 1")
    Alarm getById(int id);

    @Query("UPDATE Alarms SET endTime=:endTime, enableMission=:enableMission, ringtoneId=:prodId WHERE id=:id")
    void update(int id, long endTime, int enableMission, Integer prodId);

    @Query("DELETE FROM Alarms")
    void discardAll();

    @Query("DELETE FROM Alarms WHERE id IN (:id)")
    void discard(List<Integer> id);
}
