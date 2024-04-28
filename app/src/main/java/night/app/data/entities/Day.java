package night.app.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import night.app.activities.MainActivity;

@Entity(tableName = "Days")
public class Day {
    @NonNull
    @PrimaryKey
    public Long date;

    public Long startTime;

    public Long endTime;

    public String dream;

    @WorkerThread
    public static void resolveJSON(JSONArray daysInJson) {
        for (int i=0; i < daysInJson.length(); i++) {
            try {
                JSONObject json = daysInJson.getJSONObject(i);

                MainActivity.getDatabase().dayDAO().create(
                        json.getLong("date"),
                        json.getLong("startTime"),
                        json.getLong("endTime"),
                        json.getString("dream")
                );

                SleepEvent.resolveJSON(json.getJSONArray("events"));
            }
            catch (JSONException e) { System.err.println(e); }
        }
    }

    @WorkerThread
    public JSONObject toJSON() {
        try {
            JSONObject json = new JSONObject() {{
                put("date", date);
                put("startTime", startTime);
                put("endTime", endTime);
                put("dream", dream);
            }};

            SleepEvent[] events = MainActivity.getDatabase().sleepEventDAO().get(this);
            JSONObject[] eventsInJSON =
                    Arrays.stream(events).map(SleepEvent::toJSON).toArray(JSONObject[]::new);

            json.put("events", eventsInJSON);

            return json;
        }
        catch (JSONException e) {
            return null;
        }
    }
}
