package night.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import night.app.data.entities.Day;
import night.app.data.entities.Product;
import night.app.data.entities.Ringtone;
import night.app.data.entities.Theme;

@Dao
public interface AppDAO {
    // Shop Query
    @Query("SELECT * FROM Products WHERE type=:type")
    List<Product> getProducts(Integer type);

    @Query("SELECT * FROM Themes WHERE prodId=:id LIMIT 1")
    List<Theme> getTheme(int id);

    @Query("SELECT * FROM Ringtones WHERE prodId=:id LIMIT 1")
    List<Ringtone> getRingtone(int id);

    @Query("SELECT * FROM Ringtones WHERE prodId in (SELECT prodId FROM Products WHERE isBought=1)")
    List<Ringtone> getAllOwnedRingtones();

    @Query("UPDATE Products SET isBought=1 WHERE prodId=:id ")
    void updateProductStatus(int id);
}
