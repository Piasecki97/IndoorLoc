package pl.zgora.uz.indoorloc.visual;

import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.RequiresApi;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;
import pl.zgora.uz.indoorloc.model.CalibratedBluetoothDevice;

public class LocalContentWebViewClient extends WebViewClientCompat {

    private final WebViewAssetLoader mAssetLoader;
    AtomicBoolean isLoaded;

    public LocalContentWebViewClient(WebViewAssetLoader assetLoader) {
        mAssetLoader = assetLoader;
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

    }

    public void drawCurrentPosition(List<CalibratedBluetoothDevice> devices, Map<String, Double> distances) {
        StringBuilder jsDeviceShape = new StringBuilder();
        for (CalibratedBluetoothDevice dev: devices) {
            jsDeviceShape.append("var "+ dev.getFriendlyName()+" = window.addCube(5,20,5);");
            jsDeviceShape.append( dev.getFriendlyName()+" = window.addCube(5,20,5);");

        }

    }
}