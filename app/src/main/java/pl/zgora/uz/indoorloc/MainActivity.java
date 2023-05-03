package pl.zgora.uz.indoorloc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import pl.zgora.uz.indoorloc.database.DatabaseClient;
import pl.zgora.uz.indoorloc.estimote.EstimoteService;
import pl.zgora.uz.indoorloc.model.BtFoundModel;
import pl.zgora.uz.indoorloc.model.CalibratedBluetoothDevice;
import pl.zgora.uz.indoorloc.model.DeviceState;
import pl.zgora.uz.indoorloc.view.DeviceView;


public class MainActivity extends AppCompatActivity {
    public List<CalibratedBluetoothDevice> calibratedDevices = new ArrayList<>();

    public Map<String, DeviceView> devices = new HashMap<>();

    public LinearLayout ll_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        fetchCalibratedDevices();
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("MissingPermission")
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                populateDeviceViews(new BtFoundModel(result.getDevice().getName(), result.getDevice().getAddress(), result.getRssi(), false));
            }
        });
    }

    private void fetchCalibratedDevices() {
        class GetDevices extends AsyncTask<Void, Void, List<CalibratedBluetoothDevice>> {
            @Override
            protected List<CalibratedBluetoothDevice> doInBackground(Void... voids) {
                calibratedDevices = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .dataBaseAction()
                        .getAll();
                return calibratedDevices;
            }
        }
        GetDevices savedTasks = new GetDevices();
        savedTasks.execute();
    }

    public void populateDeviceViews(BtFoundModel bfm) {
        if(devices.containsKey(bfm.macAddress)) {
            DeviceView dv = devices.get(bfm.macAddress);

            if(dv != null) {
                for (CalibratedBluetoothDevice calDev: calibratedDevices) {
                    if(calDev.getMacAddress().equals(bfm.macAddress)) {
                        dv.setRefferenceRssi(calDev.getMeasuredPower());
                        dv.setMacAddress(calDev.getMacAddress());
                        TextView dtv = dv.getDeviceNameView();
                        dv.getBt().setText(DeviceState.Configured.label);
                        dv.setDeviceNameView(dtv);
                        dv.setCalibratedBluetoothDevice(calDev);
                    }
                }
                dv.setRssiPowerText(bfm.rssi);
            }
        } else if(bfm.name != null && !bfm.name.isEmpty()) {
            DeviceView view =
                    new DeviceView(ll_layout.getContext(),bfm.macAddress, bfm.rssi);
            if(bfm.isEstimote) {
                view.getDeviceNameView().setTypeface(null, Typeface.BOLD_ITALIC);
            }
            ll_layout.addView(view);
            devices.put(bfm.macAddress, view);
        }
    }

}