package pl.zgora.uz.indoorloc.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import pl.zgora.uz.indoorloc.R;
import pl.zgora.uz.indoorloc.estimote.EstimoteService;

public class DeviceView extends LinearLayout {

    private TextView deviceNameView;
    private TextView rssiView;

    private Integer refferenceRssi;
    private Button bt;
    private String deviceName;
    private Integer rssi;

    public TextView getDeviceNameView() {
        return deviceNameView;
    }

    public void setDeviceNameView(TextView deviceNameView) {
        this.deviceNameView = deviceNameView;
    }

    public void setRssiPowerText(Integer rssi) {
        String text = Integer.toString(rssi);
        System.out.println(deviceName + "  >>>>>>>>REF ID IS " + refferenceRssi);
        if (refferenceRssi != null) {
            Double distance = EstimoteService.rssiToDistance(rssi, refferenceRssi, 3);
            DecimalFormat df = new DecimalFormat("#.##");
            text = df.format(distance) + " m.";
            bt.setText("Calibrated");
            bt.setTextSize(9);
            bt.setBackgroundColor(Color.GREEN);
        }
        rssiView.setText(text);
    }

    public TextView getRssiView() {
        return rssiView;
    }

    public void setRssiView(TextView rssiView) {
        this.rssiView = rssiView;
    }

    public DeviceView(Context context, String deviceName, Integer rssi) {
        super(context);
        this.setOrientation(LinearLayout.HORIZONTAL);
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
        t.start();
        Button confirm = dialog.findViewById(R.id.confirm);

        confirm.setOnClickListener(view -> {
            refferenceRssi = Integer.parseInt(getRssiView().getText().toString());
            t.interrupt();
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
}
