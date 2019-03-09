package com.linklab.emmanuelogunjirin.besi_c;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class EndOfDayPrompt2 extends WearableActivity {

    private Button Proceed, Snooze, Dismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_day_prompt);

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
}
