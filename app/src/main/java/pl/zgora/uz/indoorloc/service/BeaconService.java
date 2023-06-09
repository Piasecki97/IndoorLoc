package pl.zgora.uz.indoorloc.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import pl.zgora.uz.indoorloc.MainActivity;
import pl.zgora.uz.indoorloc.model.BtFoundModel;
import pl.zgora.uz.indoorloc.model.CalibratedBluetoothDevice;
import pl.zgora.uz.indoorloc.trilateration.NonLinearLeastSquaresSolver;
import pl.zgora.uz.indoorloc.trilateration.TrilaterationFunction;

public class BeaconService {
    public static Map<String, Double> devicesDistances = new HashMap<>();
    public static double[] previousPosition = null;
    Boolean positionsSet = false;


    public double[] calculatePosition(List<CalibratedBluetoothDevice> calibratedBluetoothDeviceList) {
        double[][] positions = new double[calibratedBluetoothDeviceList.size()][3];
        double[] distances = new double[calibratedBluetoothDeviceList.size()];
        int i = 0;
        for (CalibratedBluetoothDevice device : calibratedBluetoothDeviceList) {
            if (devicesDistances.get(device.getMacAddress()) != null) {
                positionsSet = true;
                positions[i][0] = device.getX();
                positions[i][1] = device.getY();
                positions[i][2] = device.getZ();
                distances[i] = devicesDistances.get(device.getMacAddress());
                i++;
            } else {
                Log.println(Log.WARN, this.getClass().getName(), "Failed to get distance to device" + device.getFriendlyName());
            }
        }


        TrilaterationFunction trilaterationFunction = new TrilaterationFunction(positions, distances);
        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(trilaterationFunction, new LevenbergMarquardtOptimizer());
        double[] calculatedPosition = solver.solve().getPoint().toArray();
        if (previousPosition != null) {
            Double distance = Math.sqrt(Math.pow(calculatedPosition[0] - previousPosition[0], 2) + Math.pow(calculatedPosition[1] - previousPosition[1], 2) + Math.pow(calculatedPosition[2] - previousPosition[2], 2));
            // we measure each second so calculate velocity in m/s
            // if > 10km/h which is approx 2.7 m/s ignore this read
            Double velocity = distance/100;
            if (velocity > 2.7) {
                calculatedPosition = previousPosition;
            }
        } else {
            previousPosition = calculatedPosition;
        }
        return calculatedPosition;
    }


    public void findDevice(MainActivity activity) {
        BluetoothManager bluetoothManager = activity.getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            activity.startActivityForResult(enableBtIntent, 0);
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);


        }
        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(new ScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("MissingPermission")
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                activity.populateDeviceViews(new BtFoundModel(result.getDevice().getName(), result.getDevice().getAddress(), result.getRssi(), false));
            }
        });
    }
}
