package com.linklab.INERTIA.besi_c;

// Imports
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;


/* ************************************************************************************* MAIN ACTIVITY OF THE APP ************************************************************************************************** */
public class MainActivityEODEMA extends WearableActivity  // This is the activity that runs on the main screen. This is the main User interface and dominates the start of the app
{
    @Override
    protected void onCreate(Bundle savedInstanceState)      // This is created on startup
    {
        super.onCreate(savedInstanceState);      // Creates the main screen.
        setContentView(R.layout.activity_main_eodema);     // This is where the texts and buttons seen were made. (Look into: res/layout/activity_main_sleep)
    }
}