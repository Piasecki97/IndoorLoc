package pl.zgora.uz.indoorloc;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv;
    TableLayout ll = findViewById(R.id.table);
    BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

    private BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    private boolean scanning;
    private Handler handler = new Handler();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

//    private void scanLeDevice() {
//        if (!scanning) {
//            // Stops scanning after a predefined scan period.
//            handler.postDelayed(() -> {
//                scanning = false;
//                bluetoothLeScanner.stopScan(leScanCallback);
//            }, SCAN_PERIOD);
//
//            scanning = true;
//            bluetoothLeScanner.startScan(leScanCallback);
//        } else {
//            scanning = false;
//            bluetoothLeScanner.stopScan(leScanCallback);
//        }
//    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADMIN}, 0);
                }
                TableRow row = new TableRow(context);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                String deviceName = device.getName();
                TextView deviceNameView = new TextView(context);
                deviceNameView.setText(deviceName);
                row.addView(deviceNameView);

                String deviceHardwareAddress = device.getAddress(); // MAC address
                TextView deviceHardwareAddressView = new TextView(context);
                deviceHardwareAddressView.setText(deviceHardwareAddress);
                row.addView(deviceNameView);

                String rssi = Short.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
                TextView rssiView = new TextView(context);
                rssiView.setText(rssi);
                row.addView(rssiView);

                ll.addView(row);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.displayLinear);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADMIN}, 0);
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBtIntent, 0);
        }

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }
}