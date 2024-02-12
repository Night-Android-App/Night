package night.app.data;

import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
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
    public Integer accent = 0xFF441E9F;
    public Integer onPrimary = 0xFFFFFFFF;
    public Integer onSurface = 0xFF000000;

    public int getOnPrimaryVariant() {
        return darken(onPrimary, 0.4);
    }

    public int getSurfaceVariant() {
        return darken(surface, 0.08);
    }

    public static int darken(int color, double fraction) {
        return Color.argb(
                Color.alpha(color),
                darkenColor(Color.red(color), fraction),
                darkenColor(Color.green(color), fraction),
                darkenColor(Color.blue(color), fraction)
        );
    }

    private static int darkenColor(int color, double fraction) {
        return (int) Math.max(color - (color * fraction), 0);
    }

    public ColorStateList getSwitchThumbColors() {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];

        states[0] = new int[] { android.R.attr.state_checked };
        colors[0] = accent;

        states[1] = new int[0];
        colors[1] = primary;

        return new ColorStateList(states, colors);
    }

    public ColorStateList getSwitchTrackColors() {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];

        states[0] = new int[] { android.R.attr.state_checked };
        colors[0] = accent & 0x00FFFFFF | 0x25000000;

        states[1] = new int[0];
        colors[1] = getOnPrimaryVariant();

        return new ColorStateList(states, colors);
    }

    public ColorStateList getOnPrimaryColorState() {
        return ColorStateList.valueOf(onPrimary);
    }
}
