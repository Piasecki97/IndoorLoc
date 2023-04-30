package pl.zgora.uz.indoorloc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import pl.zgora.uz.indoorloc.estimote.EstimoteService;
import pl.zgora.uz.indoorloc.view.DeviceView;

public class MainActivity extends AppCompatActivity {
    public Map<String, DeviceView> devices = new HashMap<>();

    public LinearLayout ll_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ll_layout = findViewById(R.id.ll_layout);
        EstimoteService service = new EstimoteService(this);


        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, 0);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);


        }
        service.findDevice(getBaseContext());
        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(new ScanCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                String btAddress = result.getDevice().getAddress();
                if(devices.containsKey(btAddress)) {
                    DeviceView dv = devices.get(btAddress);
                    if(dv != null) {
                        dv.setRssiPowerText(result.getRssi());
                    }
                } else if(result.getDevice().getName() != null && !result.getDevice().getName().isEmpty()) {
                    DeviceView view =
                            new DeviceView(ll_layout.getContext(), result.getDevice().getName(), result.getRssi());
                    ll_layout.addView(view);
                    devices.put(result.getDevice().getAddress(), view);
                }
            }
        });
    }


}