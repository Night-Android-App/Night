package night.app.utils;

import android.graphics.Color;

public class ColorUtils {
    public static int darken(int color, float fraction) {
        return Color.argb(
                Color.alpha(color),
                (int) (Color.red(color) * fraction),
                (int) (Color.green(color) * fraction),
                (int) (Color.blue(color) * fraction)
        );
    }

    public static int alpha(int color, float fraction) {
        return Color.argb(
                (int)(Color.alpha(color) * fraction),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }
}
