package pl.zgora.uz.indoorloc.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import pl.zgora.uz.indoorloc.R;
import pl.zgora.uz.indoorloc.model.dto.LocConfig;

public class ConfigEntry extends View {
    private TextView id;
    private TextView configName;
    private Button button;



    public ConfigEntry(Context context, String id, String configName, LocConfig config) {
        super(context);


    }

}
