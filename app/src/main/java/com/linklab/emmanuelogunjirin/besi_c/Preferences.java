package com.linklab.emmanuelogunjirin.besi_c;


public class Preferences
{
    public Preferences(){}

    // Role of user wearing the watch
    public String Role = "PT"; // CG for Caregiver or PT for Patient


    // Settings for Pain EMA | Time is in ms |
    public int PainEMAReminderDelay = 0; // How long to give the user before starting the timer to remind user to complete the ema
    public int PainEMAReminderInterval = 30000; // How long to wait between each ping reminding user to complete ema
    public int PainEMAReminderNumber = 2; // How many times to remind the wearer to complete ema

    // Settings for Follow-up EMA | Time is in ms |
    public int FollowUpEMADelay = 5*60*1000; // How long to wait after PainEMA submitted to prompt follow-up ema

    // Settings for Daily EMA
    // ----------------------------- // At what time to prompt wearer to complete daily ema
    //

    // Settings for Heart Rate Monitoring | Time is in ms |
    public int HRSampleDuration = 30000;   // How long should heart rate be measured each time?
    public int HRMeasurementInterval = 5 * 60 * 1000;  // Every how often should a measurement be taken?

    // Settings for Log Files -- NOT YET IMPLEMENTED
    public String EndOfDay_EMA_Activity_Headers = ""; // Column Headers for EndOfDay_EMA_Activity
    public String EndOfDay_EMA_Results_Headers = ""; // Column Headers for EndOfDay_EMA_Results
    public String Pain_EMA_Activity_Headers = ""; // Column Headers for Pain_EMA_Activity
    public String Pain_EMA_Results_Headers = ""; // Column Headers for Pain_EMA_Results
    public String Followup_EMA_Activity_Headers = ""; // Column Headers for Followup_EMA_Activity
    public String Followup_EMA_Results_Headers = ""; // Column Headers for Followup_EMA_Results
    public String Heart_Rate_Data_Headers = ""; // Column Headers for Heart_Rate_Data
    public String Accelerometer_Data_Headers = ""; // Column Headers for Accelerometer_Data
    public String Pedometer_Data_Headers = ""; // Column Headers for Pedometer_Data
    public String Estimote_Data_Headers = ""; // Column Headers for Estimote_Data


}
