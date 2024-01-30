package night.app.data;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ThemeDAO {
    @Query("SELECT * FROM theme WHERE id=:id")
    List<Theme> getTheme(String id);

    @Query("SELECT * FROM theme")
    List<Theme> count();

    @Query("INSERT INTO theme (id, `primary`, secondary, onSecondary, onSecondaryVariant, accent, text, textInactive, textContrast) VALUES (:id, :pr, :sec, :onSec, :onSecVar, :accent, :txt, :txtInact, :txtCf)")
     void insertTheme(String id, int pr, int sec, int onSec, int onSecVar, int accent, int txt, int txtInact, int txtCf);
}
