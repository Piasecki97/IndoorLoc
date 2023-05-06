package pl.zgora.uz.indoorloc;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;
import io.github.sceneview.SceneView;
import jmini3d.Renderer3d;
import jmini3d.ScreenController;
import jmini3d.android.input.InputController;
import pl.zgora.uz.indoorloc.database.DatabaseClient;
import pl.zgora.uz.indoorloc.estimote.EstimoteService;
import pl.zgora.uz.indoorloc.model.BtFoundModel;
import pl.zgora.uz.indoorloc.model.CalibratedBluetoothDevice;
import pl.zgora.uz.indoorloc.model.DeviceState;
import pl.zgora.uz.indoorloc.service.BeaconService;
import pl.zgora.uz.indoorloc.view.DeviceView;
import pl.zgora.uz.indoorloc.visual.LocalContentWebViewClient;


public class MainActivity extends AppCompatActivity {
    public List<CalibratedBluetoothDevice> calibratedDevices = new ArrayList<>();

    public Set<String> devices = new HashSet<>();

    public LinearLayout ll_layout;

    public Button calculatePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        fetchCalibratedDevices();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ll_layout = findViewById(R.id.ll_layout);
        calculatePosition = findViewById(R.id.calculatePosition);
        EstimoteService estimoteService = new EstimoteService(this);
        BeaconService beaconService = new BeaconService();


        estimoteService.findDevice(getBaseContext());
//        beaconService.findDevice(this);

        calculatePosition.setOnClickListener(listener -> {
            fetchCalibratedDevices();

            showWebviewModal(beaconService);
//            if (calibratedDevices.size() < 4) {
//                Toast.makeText(getApplicationContext(), "You need more than 3 devices to calculate position", Toast.LENGTH_LONG).show();
//            } else {
//                showPositionModal(beaconService);
//            }
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
        CalibratedBluetoothDevice relatedCalibratedDevice = null;
        if (devices.contains(bfm.macAddress)) {
            DeviceView dv = findViewById(bfm.macAddress.hashCode());

            for (CalibratedBluetoothDevice calDev : calibratedDevices) {
                if (calDev.getMacAddress().equals(bfm.macAddress)) {
                    relatedCalibratedDevice = calDev;
                    if (dv != null) {
                        dv.setRefferenceRssi(calDev.getMeasuredPower());
                        dv.setMacAddress(calDev.getMacAddress());
                        TextView dtv = dv.getDeviceNameView();
                        dv.getBt().setText(DeviceState.Configured.label);
                        dv.setDeviceNameView(dtv);
                        dv.setCalibratedBluetoothDevice(calDev);
                    }
                }
            }
            if (dv != null) {
                dv.setRssiPowerText(bfm.rssi);
                dv.invalidate();
            }
            if(relatedCalibratedDevice != null) {
                BeaconService.devicesDistances.put(bfm.macAddress, EstimoteService.rssiToDistance(bfm.rssi,relatedCalibratedDevice.getMeasuredPower(), 3));
            }
        } else if (bfm.name != null && !bfm.name.isEmpty()) {
            DeviceView view =
                    new DeviceView(ll_layout.getContext(), bfm.name, bfm.macAddress, bfm.rssi);
            if (bfm.isEstimote) {
                view.getDeviceNameView().setTypeface(null, Typeface.BOLD_ITALIC);
            }
            ll_layout.addView(view);
            devices.add(bfm.macAddress);
        }
    }

    void showWebviewModal(BeaconService service) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.three_js_activity);
        WebView wv = dialog.findViewById(R.id.webView);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setAllowContentAccess(true);
        wv.getSettings().setAllowFileAccessFromFileURLs(true);
        wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
        WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this))
                .build();
        wv.setWebViewClient(new LocalContentWebViewClient(assetLoader));
        wv.loadUrl("file:///android_asset/index.html");
        dialog.show();
    }
    void showPositionModal(BeaconService service) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.position_view);
        AtomicBoolean runCalculations = new AtomicBoolean(true);
        TextView tx = dialog.findViewById(R.id.curX);
        TextView ty = dialog.findViewById(R.id.curY);
        TextView tz = dialog.findViewById(R.id.curZ);
        Button ok = dialog.findViewById(R.id.okBtn);
        Thread t = new Thread() {
            @Override
            public void run() {
                while (runCalculations.get()) {
                    double[] positions = service.calculatePosition(calibratedDevices);
                    runOnUiThread(() -> {
                        tx.setText(String.valueOf(positions[0]));
                        ty.setText(String.valueOf(positions[1]));
                        tz.setText(String.valueOf(positions[2]));
                    });
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        };
        dialog.show();

        t.start();
        ok.setOnClickListener(listener -> {
            runCalculations.set(false);
            dialog.dismiss();
        });
    }
}