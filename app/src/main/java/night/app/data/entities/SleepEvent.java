package night.app.data.entities;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import night.app.activities.MainActivity;

@Entity(tableName = "SleepEvents")
public class SleepEvent {
    @PrimaryKey @NonNull
    public long timeline;

    @NonNull
    public int confidence;

    @NonNull
    public int motion;

    @NonNull
    public int light;

    @WorkerThread
    public static void resolveJSON(JSONArray eventsInJSON) {
        for (int i=0; i < eventsInJSON.length(); i++) {
            try {
                JSONObject json = eventsInJSON.getJSONObject(i);

                MainActivity.getDatabase().sleepEventDAO().create(
                        json.getLong("timeline"),
                        json.getInt("confidence"),
                        json.getInt("motion"),
                        json.getInt("light")
                );
            }
            catch (JSONException e) { System.err.println(e); }
        }
    }

    public JSONObject toJSON() {
        try {
            JSONObject json = new JSONObject() {{
                put("timeline", timeline);
                put("confidence", confidence);
                put("motion", motion);
                put("light", light);
            }};

            return json;
        }
        catch (JSONException e) {
            return null;
        }
    }

    public SleepEvent(long timeline, int confidence, int motion, int light) {
        this.timeline = timeline;
        this.confidence = confidence;
        this.motion = motion;
        this.light = light;
    }
}
