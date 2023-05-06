package pl.zgora.uz.indoorloc.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import pl.zgora.uz.indoorloc.R;
import pl.zgora.uz.indoorloc.database.DatabaseClient;
import pl.zgora.uz.indoorloc.estimote.EstimoteService;
import pl.zgora.uz.indoorloc.model.CalibratedBluetoothDevice;

public class DeviceView extends LinearLayout {

    private TextView deviceNameView;
    private TextView rssiView;
    private CalibratedBluetoothDevice calibratedBluetoothDevice;
    private Integer refferenceRssi;



    private Button bt;
    private String deviceName;

    private String macAddress;
    private Integer rssi;

    public TextView getDeviceNameView() {
        return deviceNameView;
    }

    public void setDeviceNameView(TextView deviceNameView) {
        this.deviceNameView = deviceNameView;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public void setRssiPowerText(Integer rssi) {
        this.rssi = rssi;
        String text = Integer.toString(rssi);
        if (calibratedBluetoothDevice != null) {
            Double distance = EstimoteService.rssiToDistance(rssi, refferenceRssi, 3);
            calibratedBluetoothDevice.setDistance(distance);
            DecimalFormat df = new DecimalFormat("#.##");
            text = df.format(distance) + " m.";
            bt.setText("Calibrated");
            bt.setTextSize(9);
            bt.setBackgroundColor(Color.GREEN);
            deviceNameView.setText(calibratedBluetoothDevice.getFriendlyName());
        }
        rssiView.setText(text);
    }

    public TextView getRssiView() {
        return rssiView;
    }

    public void setRssiView(TextView rssiView) {
        this.rssiView = rssiView;
    }

    public DeviceView(Context context, String deviceName, String macAddress, Integer rssi) {
        super(context);
        this.setId(macAddress.hashCode());
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.macAddress = deviceName;
        this.deviceName = deviceName;
        this.rssi = rssi;
        deviceNameView = new TextView(this.getContext());
        rssiView = new TextView(this.getContext());
        bt = new Button(this.getContext());
        deviceNameView.setText(deviceName);
        setRssiPowerText(rssi);
        rssiView.setGravity(Paint.Align.CENTER.ordinal());
        deviceNameView.setWidth(600);
        deviceNameView.setMinWidth(600);
        deviceNameView.setTextSize(21);
        rssiView.setTextSize(21);
        rssiView.setMinWidth(430);
        bt.setText("Calibrate");
        bt.setWidth(430);
        bt.setMinWidth(430);
        bt.setOnClickListener(l -> {
            showCalibrateDeviceModal();
        });

        this.addView(deviceNameView);
        this.addView(rssiView);
        this.addView(bt);
    }

    void showCalibrateDeviceModal() {
        final Dialog dialog = new Dialog(getRootView().getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.calibrate_device);

        TextView tv = dialog.findViewById(R.id.rssi);
        EditText tname = dialog.findViewById(R.id.friendlyName);
        EditText tx = dialog.findViewById(R.id.x);
        EditText ty = dialog.findViewById(R.id.y);
        EditText tz = dialog.findViewById(R.id.z);
        if(calibratedBluetoothDevice != null) {
            tname.setText(calibratedBluetoothDevice.getFriendlyName());
            tx.setText(calibratedBluetoothDevice.getX().toString());
            ty.setText(calibratedBluetoothDevice.getY().toString());
            tz.setText(calibratedBluetoothDevice.getZ().toString());
        }
            Thread t = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        tv.setText(getRssiView().getText());
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            return;
                        }

                    }
                }
            };
        if(calibratedBluetoothDevice == null) {
            t.start();
        } else {
            tv.setText("Device calibrated to: " + calibratedBluetoothDevice.getMeasuredPower().toString());
        }
        Button confirm = dialog.findViewById(R.id.confirm);

        confirm.setOnClickListener(view -> {
            String name = tname.getText().toString();
            Double x = Double.parseDouble(tx.getText().toString());
            Double y = Double.parseDouble(ty.getText().toString());
            Double z = Double.parseDouble(tz.getText().toString());
            if (calibratedBluetoothDevice == null) {
                t.interrupt();
            }
            if(calibratedBluetoothDevice == null) {
                refferenceRssi = Integer.parseInt(getRssiView().getText().toString());
            }
            calibratedBluetoothDevice = new CalibratedBluetoothDevice(macAddress, name, refferenceRssi, x, y, z);
            class InsertDevices extends AsyncTask<Void, Void, Integer> {
                @Override
                protected Integer doInBackground(Void... voids) {
                     DatabaseClient
                            .getInstance(getContext().getApplicationContext())
                            .getAppDatabase()
                            .dataBaseAction()
                            .insertAll(calibratedBluetoothDevice);
                    return 0;
                }
            }
            InsertDevices ins = new InsertDevices();
            ins.execute();
            deviceNameView.setText(name);
            dialog.dismiss();
        });

        dialog.show();
    }

    public Integer getRefferenceRssi() {
        return refferenceRssi;
    }

    public void setRefferenceRssi(Integer refferenceRssi) {
        this.refferenceRssi = refferenceRssi;
    }


    public void setCalibratedBluetoothDevice(CalibratedBluetoothDevice calibratedBluetoothDevice) {
        this.calibratedBluetoothDevice = calibratedBluetoothDevice;
    }
    public Button getBt() {
        return bt;
    }
}
