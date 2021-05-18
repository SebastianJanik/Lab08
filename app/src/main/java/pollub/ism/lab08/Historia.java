package pollub.ism.lab08;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Historia")
public class Historia {
    @PrimaryKey(autoGenerate = true)
    public int _id;
    public String nazwa;
    public String data;
    public int stara_ilosc;
    public int nowa_ilosc;
}
