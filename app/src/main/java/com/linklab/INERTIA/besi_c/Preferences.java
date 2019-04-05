package com.linklab.INERTIA.besi_c;

// Imports

public class Preferences
{
    // Settings for Deployment
    String DeviceID = "Fossil_41mm";
    String DeploymentID = "Test1";
    String Role = "PT";  // Role of user wearing the watch; CG for Caregiver or PT for Patient

    // Settings for Pain EMA | Time is in ms |
    public int PainEMAReminderDelay = 0; // How long to give the user before starting the timer to remind user to complete the ema
    public int PainEMAReminderInterval = 5*60*1000; // How long to wait between each ping reminding user to complete ema
    public int PainEMAReminderNumber = 2; // How many times to remind the wearer to complete ema

    // Settings for Follow-up EMA | Time is in ms |
    public int FollowUpEMADelay = 30*60*1000; // How long to wait after PainEMA submitted to prompt follow-up ema
    public int FollowUpEMAReminderNumber = 3; // How many times to remind the wearer to complete the follow-up ema
    public int FollowUpEMAReminderInterval = 5*60*1000; // How long to wait between each ping reminding user to complete ema

    // Settings for Daily EMA
    public long EoDPrompt_TimeOut = 15*60*1000; // Delay before snoozing and eventually dismissing daily ema
    public int EoDEMA_Time_Hour = 21; // Hour at which the daily ema should go off
    public int EoDEMA_Time_Minute = 0; // Minute of hour at which daily ema should go off
    public int EoDEMA_Time_Second = 0; // Second of minute at which daily ema should go off
    public long EoDEMA_Timer_Delay = 10 * 60 * 1000; // This is how often the timer waits before firing again
    public long EoDEMA_Period = 24*60*60*1000; // This is how often the timer waits before firing again

    // Settings for Heart Rate Monitoring | Time is in ms |
    public long HRSampleDuration = 30000;   // How long should heart rate be measured each time?
    public long HRMeasurementInterval = 5*60*1000;  // Every how often should a measurement be taken?

    /* <--- This has been set to a near continuous stream with watch not crashing, please do not touch -----> */
    // Settings for Estimote | Time is in ms |
    public long ESSampleDuration = 30000;   // How long should estimote be measured each time?
    public long ESMeasurementInterval = 60000;  // Every how often should a measurement be taken?

/* Settings for Log Files -- NOT YET IMPLEMENTED */
//    public String EndOfDay_EMA_Activity_Headers = ""; // Column Headers for EndOfDay_EMA_Activity
//    public String EndOfDay_EMA_Results_Headers = ""; // Column Headers for EndOfDay_EMA_Results
//    public String Pain_EMA_Activity_Headers = ""; // Column Headers for Pain_EMA_Activity
//    public String Pain_EMA_Results_Headers = ""; // Column Headers for Pain_EMA_Results
//    public String Followup_EMA_Activity_Headers = ""; // Column Headers for Followup_EMA_Activity
//    public String Followup_EMA_Results_Headers = ""; // Column Headers for Followup_EMA_Results
//    public String Heart_Rate_Data_Headers = ""; // Column Headers for Heart_Rate_Data
//    public String Accelerometer_Data_Headers = ""; // Column Headers for Accelerometer_Data
//    public String Pedometer_Data_Headers = ""; // Column Headers for Pedometer_Data
//    public String Estimote_Data_Headers = ""; // Column Headers for Estimote_Data
}