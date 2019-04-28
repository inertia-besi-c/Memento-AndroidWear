package com.linklab.INERTIA.besi_c;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LowBattery extends WearableActivity {

    private Button dismiss;
    private Vibrator vibrator;
    private int vDuration = new Preferences().LowBatBuzzDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_battery);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        vibrator.vibrate(vDuration);

        dismiss = findViewById(R.id.Dismiss);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Enables Always-on
        setAmbientEnabled();
    }
}
