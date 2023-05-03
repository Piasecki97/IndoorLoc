package pl.zgora.uz.indoorloc.estimote;

import android.content.Context;
import android.graphics.Typeface;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.recognition.packets.ConfigurableDevice;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import java.util.UUID;

import pl.zgora.uz.indoorloc.MainActivity;
import pl.zgora.uz.indoorloc.model.BtFoundModel;
import pl.zgora.uz.indoorloc.view.DeviceView;

public class EstimoteService {

    static BeaconRegion region;
    static {
        region = new BeaconRegion("test", UUID.randomUUID(), null, null);
    }
    MainActivity activity;

    public EstimoteService(MainActivity activity) {
        this.activity = activity;
    }

    public void findDevice(Context context) {

        BeaconManager beaconManager = new BeaconManager(context);


        beaconManager.setForegroundScanPeriod(500, 300);
        beaconManager.setBackgroundScanPeriod(500, 300);

        beaconManager.connect(() -> {
            beaconManager.setConfigurableDevicesListener(configurableDevices -> {
                for (ConfigurableDevice device : configurableDevices) {
                    activity.populateDeviceViews(new BtFoundModel(device.deviceId.toString().substring(0, 15), device.deviceId.toString(), device.rssi, true));
                }
            });
            beaconManager.startConfigurableDevicesDiscovery();
        });
    }


    public static double rssiToDistance(int rssiMeasured, int rssiRef, double n) {
        return Math.pow(10, (rssiRef - rssiMeasured) / (10 * n));
    }
}

