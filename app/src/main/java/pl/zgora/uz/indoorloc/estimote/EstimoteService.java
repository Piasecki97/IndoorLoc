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


        // this will make the beacon attempt to detect Connectivity packets for 10 seconds,
        // then wait 5 seconds before the next detection, then repeat the cycle
        beaconManager.setForegroundScanPeriod(10000, 5000);
        beaconManager.setBackgroundScanPeriod(10000, 5000);

        // this connects to an underlying service, not to the beacon (-;
        beaconManager.connect(() -> {
            beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
                @Override
                public void onEnteredRegion(BeaconRegion beaconRegion, List<Beacon> beacons) {
                    for (Beacon b: beacons) {
                        if(activity.devices.containsKey(b.getMacAddress())) {
                            activity.devices.get(b.getMacAddress()).setRefferenceRssi(b.getMeasuredPower());
                        }
                    }
                }

                @Override
                public void onExitedRegion(BeaconRegion beaconRegion) {
                    // Do nothing
                }
            });
            beaconManager.setConfigurableDevicesListener(configurableDevices -> {
                for (ConfigurableDevice device : configurableDevices) {
                    String address = device.macAddress.toStandardString();
                    if (activity.devices.containsKey(address)) {
                        DeviceView dv = activity.devices.get(address);
                        dv.setRssiPowerText(device.rssi);
                    } else {
                        DeviceView view =
                                new DeviceView(context, device.deviceId.toString().substring(0,15), device.rssi);
                        view.getDeviceNameView().setTypeface(null, Typeface.BOLD_ITALIC);
                        activity.ll_layout.addView(view);
                        activity.devices.put(address, view);
                    }
                }
            });
            beaconManager.startConfigurableDevicesDiscovery();
        });
    }


    public static double rssiToDistance(int rssiMeasured, int rssiRef, double n) {
        return Math.pow(10, (rssiRef - rssiMeasured) / (10 * n));
    }
}

