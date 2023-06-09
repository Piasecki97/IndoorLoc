package pl.zgora.uz.indoorloc.model.dto;

import java.util.List;

import pl.zgora.uz.indoorloc.model.CalibratedBluetoothDevice;

public class LocConfig {
    private Long id;
    private String configName;
    private List<CalibratedBluetoothDevice> devices;
}
