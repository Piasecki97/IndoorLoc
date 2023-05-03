package pl.zgora.uz.indoorloc.model;

public class BtFoundModel {
    public String name;
    public String macAddress;
    public Integer rssi;
    public Boolean isEstimote;

    public BtFoundModel(String name, String macAddress, Integer rssi, Boolean isEstimote) {
        this.name = name;
        this.macAddress = macAddress;
        this.rssi = rssi;
        this.isEstimote = isEstimote;
    }
}
