package com.linklab.emmanuelogunjirin.besi_c;

import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class EndOfDayPrompt2 extends WearableActivity {

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
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayPrompt3.class);
                        startActivity(StartEMAActivity);
                    }
                },new Preferences().EoDEMA_Timer_Delay);
                finish();
            }

        });
        Dismiss.setOnClickListener(new View.OnClickListener() {
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
