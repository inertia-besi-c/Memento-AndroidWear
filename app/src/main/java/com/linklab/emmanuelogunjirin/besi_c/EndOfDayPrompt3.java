package com.linklab.emmanuelogunjirin.besi_c;

import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class EndOfDayPrompt3 extends WearableActivity {

    private Button Proceed, Snooze, Dismiss;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_day_prompt);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "HRService:wakeLock");
        wakeLock.acquire();

        Proceed = findViewById(R.id.Proceed);
        Snooze = findViewById(R.id.Snooze);
        Dismiss = findViewById(R.id.Dismiss);

        Dismiss.setVisibility(View.INVISIBLE);

        Snooze.setText("Dismiss");
        Snooze.setBackgroundColor(getResources().getColor(R.color.dark_red));

        Proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayEMA.class);      // Links to the EMA File
                startActivity(StartEMAActivity);

                finish();
            }
        });

        Snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onDestroy()
    {
        wakeLock.release();
        super.onDestroy();
    }
}
