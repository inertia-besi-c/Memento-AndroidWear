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

public class EndOfDayPrompt3 extends WearableActivity
{
    private PowerManager.WakeLock wakeLock;
    @SuppressLint({"WakelockTimeout", "SetTextI18n"})

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_day_prompt);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "EOD Prompt 3:wakeLock");
        wakeLock.acquire();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(600);

        Button proceed = findViewById(R.id.Proceed);
        Button snooze = findViewById(R.id.Snooze);
        Button dismiss = findViewById(R.id.Dismiss);

        dismiss.setVisibility(View.INVISIBLE);

        snooze.setText("Dismiss");
        snooze.setBackgroundColor(getResources().getColor(R.color.dark_red));

        snooze.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        proceed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayEMA.class);      // Links to the EMA File
                startActivity(StartEMAActivity);
                finish();
            }
        });
        setAmbientEnabled();
    }

    @Override
    public void onDestroy()
    {
        wakeLock.release();
        super.onDestroy();
    }
}
