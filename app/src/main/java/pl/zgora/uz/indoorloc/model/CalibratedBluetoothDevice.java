package pl.zgora.uz.indoorloc.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "devices")
public class CalibratedBluetoothDevice {
    @PrimaryKey
    @NonNull
    private final String macAddress;

    private final String friendlyName;
    private final Integer measuredPower;
    private final Double x;
    private final Double y;
    private final Double z;

    @Ignore
    private Double distance;

    public CalibratedBluetoothDevice(String macAddress, String friendlyName, Integer measuredPower, Double x, Double y, Double z) {
        this.macAddress = macAddress;
        this.friendlyName = friendlyName;
        this.measuredPower = measuredPower;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Integer getMeasuredPower() {
        return measuredPower;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

}
