package com.linklab.emmanuelogunjirin.besi_c;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class EndOfDayPrompt extends WearableActivity {

    private Button Proceed, Snooze, Dismiss;
    private PowerManager.WakeLock wakeLock;
    private Vibrator v;      // The vibrator that provides haptic feedback.


    @SuppressLint("WakelockTimeout")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_day_prompt);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "EOD Prompt 1:wakeLock");
        wakeLock.acquire();


        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(600);

        Proceed = findViewById(R.id.Proceed);
        Snooze = findViewById(R.id.Snooze);
        Dismiss = findViewById(R.id.Dismiss);
        Dismiss.setVisibility(View.INVISIBLE);

        Proceed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayEMA.class);      // Links to the EMA File
                startActivity(StartEMAActivity);

                finish();
            }
        });

        Snooze.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Timer timer = new Timer();
                timer.schedule(new TimerTask()
                {
                    @Override

                    public void run()
                    {
                        Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayPrompt2.class);
                        startActivity(StartEMAActivity);
                    }
                }, new Preferences().EoDEMA_Timer_Delay);

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
