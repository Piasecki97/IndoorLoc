package pl.zgora.uz.indoorloc.visual;

import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.RequiresApi;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;
import pl.zgora.uz.indoorloc.MainActivity;
import pl.zgora.uz.indoorloc.model.CalibratedBluetoothDevice;
import pl.zgora.uz.indoorloc.service.BeaconService;

public class LocalContentWebViewClient extends WebViewClientCompat {

    private final WebViewAssetLoader mAssetLoader;
    AtomicBoolean isLoaded;
    BeaconService beaconService;
    public List<CalibratedBluetoothDevice> calibratedDevices = new ArrayList<>();

    public LocalContentWebViewClient(WebViewAssetLoader assetLoader, BeaconService beaconService, List<CalibratedBluetoothDevice> calibratedDevices) {
        this.beaconService = beaconService;
        mAssetLoader = assetLoader;
        this.calibratedDevices = calibratedDevices;
    }

    @Override
    @RequiresApi(21)
    public WebResourceResponse shouldInterceptRequest(WebView view,
                                                      WebResourceRequest request) {
        Uri uri = request.getUrl();
        return mAssetLoader.shouldInterceptRequest(request.getUrl());
    }

    @Override
    @SuppressWarnings("deprecation") // to support API < 21
    public WebResourceResponse shouldInterceptRequest(WebView view,
                                                      String url) {
        return mAssetLoader.shouldInterceptRequest(Uri.parse(url));
    }

    @Override
    public void onPageFinished(WebView wv, String url) {
        super.onPageFinished(wv, url);
        drawBeacons(wv);
        Thread t = new Thread() {
            @Override
            public void run() {
                while(true) {
                    wv.post(() -> {
                        double[] positions;
                        try {
                            positions = beaconService.calculatePosition(calibratedDevices);
                        } catch (Exception e ) {
                            positions = new double[]{0.0, 0.0, 0.0};
                        }
                        wv.evaluateJavascript(
                                "updateLoc("+ positions[0] + ", " + positions[1] + ", " + positions[2] + ");", null
                        );

                    });
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        t.start();


    }

    public void drawBeacons(WebView webView) {
        StringBuilder jsDeviceShape = new StringBuilder();
        for (CalibratedBluetoothDevice dev: calibratedDevices) {
            String name = dev.getFriendlyName().replace(" ", "_");
            String x = dev.getX().toString();
            String y = Double.toString(dev.getY()+10);
            String z = dev.getZ().toString();

            jsDeviceShape.append("var "+ name+" = window.addCubeAt('"+ dev.getFriendlyName() +"',5,20,5, "+x+","+y+","+z+");\n");

        }
        webView.evaluateJavascript(jsDeviceShape.toString(), null);



    }

}