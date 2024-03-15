package night.app.data;

import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import night.app.utils.ColorUtils;

@Entity(tableName="theme", foreignKeys = {
        @ForeignKey(
                entity = Product.class,
                parentColumns = "prod_id",
                childColumns = "prod_id",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        )
})
public class Theme {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "theme_name")
    public String name = "Default Theme";

    @NonNull
    @ColumnInfo(name = "prod_id")
    public Integer prodId = -1;

    public String primary;
    public String secondary;
    public String surface;
    public String accent;
    public String onPrimary;
    public String onSurface;

    public Integer getPrimary() {
        return primary == null ? 0xFF212529 : Color.parseColor(primary);
    }

    public Integer getSecondary() {
        return secondary == null ? 0xFFF5F5F5 :Color.parseColor(secondary);
    }

    public Integer getSurface() {
        return surface == null ? 0xFFFFFFFF : Color.parseColor(surface);
    }

    public Integer getAccent() {
        return accent == null ? 0xFF441E9F : Color.parseColor(accent);
    }

    public Integer getOnPrimary() {
        return onPrimary == null ? 0xFFFFFFFF : Color.parseColor(accent);
    }
    public Integer getOnSurface() {
        return onSurface == null ? 0xFF000000 : Color.parseColor(onSurface);
    }

    public int getOnPrimaryVariant() {
        return ColorUtils.darken(getOnPrimary(), 0.6f);
    }
    public int getSurfaceVariant() {
        return ColorUtils.darken(getSurface(), 0.92f);
    }

    public ColorStateList getButtonColors() {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];

        states[0] = new int[] { android.R.attr.state_enabled };
        colors[0] = getAccent();

        states[1] = new int[] { -android.R.attr.state_enabled };
        colors[1] = ColorUtils.alpha(getOnPrimaryVariant(), 0.25f);

        return new ColorStateList(states, colors);
    }

    public ColorStateList getSwitchThumbColors() {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];

        states[0] = new int[] { android.R.attr.state_checked };
        colors[0] = getAccent();

        states[1] = new int[0];
        colors[1] = getPrimary();

        return new ColorStateList(states, colors);
    }

    public ColorStateList getSwitchTrackColors() {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];

        states[0] = new int[] { android.R.attr.state_checked };
        colors[0] = getAccent() & 0x00FFFFFF | 0x25000000;

        states[1] = new int[0];
        colors[1] = getOnPrimaryVariant();

        return new ColorStateList(states, colors);
    }

    public ColorStateList getOnPrimaryColorStateList() {
        return ColorStateList.valueOf(getOnPrimary());
    }
}
