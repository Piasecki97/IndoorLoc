package pl.zgora.uz.indoorloc.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import pl.zgora.uz.indoorloc.model.CalibratedBluetoothDevice;

@Dao
public interface ConfiguredBluetoothDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CalibratedBluetoothDevice... users);

    @Delete
    void delete(CalibratedBluetoothDevice user);

    @Query("SELECT * FROM devices")
    List<CalibratedBluetoothDevice> getAll();

    @Query("DELETE FROM devices")
    public void deleteAll();

    @Query("DELETE FROM devices WHERE macAddress IN ('[d0a9179bf056c3', '[649e49a16f0b0b')")
    public void deleteUselessDevices();
}
