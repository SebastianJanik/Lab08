package pollub.ism.lab08;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface HistoriaDAO {
    @Insert
    public void insert(Historia pozycja);

    @Update
    void update(Historia pozycja);

    @Query("SELECT _id, data, stara_ilosc, nowa_ilosc FROM Historia WHERE nazwa = :wybraneWarzywoNazwa")
    public Historia [] findLogsByName(String wybraneWarzywoNazwa);

    @Query("SELECT COUNT(*) FROM Warzywniak") //Ile jest rekordów w tabeli
    int size();
}
