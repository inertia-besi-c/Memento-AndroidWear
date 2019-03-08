package com.linklab.emmanuelogunjirin.besi_c;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class EndOfDayPrompt extends WearableActivity {

    private Button Proceed, Snooze, Dismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_day_prompt);

        Proceed = findViewById(R.id.Proceed);
        Snooze = findViewById(R.id.Snooze);
        Dismiss = findViewById(R.id.Dismiss);

        Dismiss.setVisibility(View.INVISIBLE);

        Proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayEMA.class);      // Links to the EMA File
                startActivity(StartEMAActivity);

                finish();
            }
        });

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
                        Intent StartEMAActivity = new Intent(getBaseContext(), EndOfDayPrompt.class);
                        startActivity(StartEMAActivity);
                    }
                },new Preferences().EoDEMA_Timer_Delay);
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }
}
