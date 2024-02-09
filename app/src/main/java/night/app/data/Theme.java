package night.app.data;

import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Theme {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "theme_name")
    public String name = "Default Theme";

    public Integer primary = 0xFF212529;

    public Integer secondary = 0xFFF5F5F5;

    public Integer surface = 0xFFFFFFFF;

    @ColumnInfo(name="surface_variant")
    public Integer surfaceVariant = 0xFFF0F0F0;

    public Integer accent = 0xFF441E9F;

    public Integer onPrimary = 0xFFFFFFFF;

    @ColumnInfo(name = "onPrimary_variant")
    public Integer onPrimaryVariant = 0xFF61666A;

    public Integer onSurface = 0xFF000000;

    @Ignore
    public ColorStateList getSwitchThumbColors() {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];

        states[0] = new int[] { android.R.attr.state_checked };
        colors[0] = accent;

        states[1] = new int[0];
        colors[1] = primary;

        return new ColorStateList(states, colors);
    }

    @Ignore
    public ColorStateList getSwitchTrackColors() {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];

        states[0] = new int[] { android.R.attr.state_checked };

        int color = accent;
        colors[0] = Color.argb(
                (int) Math.round(Color.alpha(color)*0.25),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );

        states[1] = new int[0];
        colors[1] = onPrimaryVariant;

        return new ColorStateList(states, colors);
    }
}
