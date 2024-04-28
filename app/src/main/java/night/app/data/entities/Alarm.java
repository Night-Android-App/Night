package night.app.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import night.app.activities.MainActivity;

@Entity(tableName = "Alarms")
public class Alarm {

    @PrimaryKey
    public Integer id;

    @NonNull
    public Integer endTime;

    public Integer ringtoneId;

    public Integer enableAlarm;

    public Integer enableMission;


    @WorkerThread
    public static void resolveJSON(JSONArray alarmsInJSON) {
        for (int i=0; i < alarmsInJSON.length(); i++) {
            try {
                JSONObject json = alarmsInJSON.getJSONObject(i);

                MainActivity.getDatabase().alarmDAO().create(
                        json.getInt("id"),
                        json.getInt("endTime"),
                        json.getInt("enableMission"),
                        json.getInt("enableAlarm"),
                        json.getInt("ringtoneId")
                );
            }
            catch (JSONException e) { System.err.println(e); }
        }
    }

    public JSONObject toJSON() {
        try {
            JSONObject json = new JSONObject() {{
                put("id", id);
                put("endTime", endTime);
                put("ringtoneId", ringtoneId);
                put("enableAlarm", enableAlarm);
                put("enableMission", enableMission);
            }};

            return json;
        }
        catch (JSONException e) {
            return null;
        }
    }
}
