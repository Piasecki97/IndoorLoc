package pl.zgora.uz.indoorloc.model;

public enum DeviceState {

    NotCalibrated("Unknown"),
    Calibrated("Calibrated"),
    Configured("Configured");
    public final String label;

    private DeviceState(String label) {
        this.label = label;
    }
}
