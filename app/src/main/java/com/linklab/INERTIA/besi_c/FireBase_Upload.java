package com.linklab.INERTIA.besi_c;

// Imports

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

@SuppressWarnings("ALL")
public class FireBase_Upload extends WearableActivity       // This is the firebase activity that is run when we need to uplaod.
{
    private Button upload;      // Button clicked that uploads the files.
    private ProgressBar uploading;      // This is the progress of the upload
    private boolean done;       // If the upload is done
    private boolean succeed;        // If the upload suceeded
    private Preferences Preference = new Preferences();     // Gets an instance from the preferences module.
    private SystemInformation SystemInformation = new SystemInformation();  // Gets an instance from the system information module
    private String PreferenceDeviceID = Preference.DeviceID;       // Gets the Device ID from preferences
    private String PreferenceDeploymentID = Preference.DeploymentID;         // Gets the deployment ID from preferences
    private String PreferenceRole = Preference.Role;       // Gets the Role from preferences
    private String Accelerometer = Preference.Accelerometer;       // Gets the Accelerometer file from preferences
    private String Battery = Preference.Battery;       // Gets the Battery level file from preferences
    private String Estimote = Preference.Estimote;       // Gets the Estimote file from preferences
    private String Pedometer = Preference.Pedometer;            // Gets the Pedometer file from preferences
    private String Pain_Activity = Preference.Pain_Activity;           // Gets the Pain Activity file from preferences
    private String Pain_Results = Preference.Pain_Results;              // Gets the Pain Results file from preferences
    private String Followup_Activity = Preference.Followup_Activity;           // Gets the Followup Activity file from preferences
    private String Followup_Results = Preference.Followup_Results;           // Gets the Followup Results file from preferences
    private String EndOfDay_Activity = Preference.EndOfDay_Activity;           // Gets the End of Day Activity file from preferences
    private String EndOfDay_Results = Preference.EndOfDay_Results;           // Gets the End of Day Results file from preferences
    private String Subdirectory_DeviceLogs = Preference.Subdirectory_DeviceLogs;        // This is where all the system logs and data are kept.
    private String Sensors = Preference.Sensors;           // Gets the Sensors file from preferences
    private String Steps = Preference.Steps;           // Gets the Steps file from preferences
    private String System = Preference.System;           // Gets the System file from preferences
    private String Heart_Rate = Preference.Heart_Rate;       // Gets the Heart Rate file from preferences
    final String timeStamp = SystemInformation.getFolderTimeStamp();      // Gets a time stamp from System information
    FirebaseStorage storage;        // The storage on firebase
    StorageReference storageRef;        // The storage reference on firebase
    PowerManager.WakeLock wakeLock;     // Wakelock
    String localDirPath = Preference.Directory;     // The directory path on the watch

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        File file = new File(Preference.Directory + SystemInformation.Sensors_Path);     // Gets the path to the accelerometer from the system.
        if (file.exists())      // If the file exists
        {
            Log.i("Firebase Service", "No Header Created");     // Logs to console
        }
        else        // If the file does not exist
        {
            Log.i("Firebase Service", "Creating Header");     // Logs on Console.

            DataLogger dataLogger = new DataLogger(Subdirectory_DeviceLogs, Sensors, Preference.Sensor_Data_Headers);        /* Logs the Accelerometer data in a csv format */
            dataLogger.LogData();       // Saves the data to the directory.
        }

        String data =  ("Firebase Service," + "Started at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Power manager calls the power distribution service.
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "End of Day EMA:wakeLock");        // It initiates a full wakelock to turn on the screen.
        wakeLock.acquire();      // The screen turns off after the timeout is passed.

        super.onCreate(savedInstanceState);     // Creates an instance of the activity
        setContentView(R.layout.activity_fire_base__upload);        // Gets a layout of the firebase upload.

        upload = findViewById(R.id.upload);     // This is the upload button
        uploading = findViewById(R.id.progressBar);     // This is the upload progress bar
        upload.setVisibility(View.INVISIBLE);       // Sets the upload button to be invisible
        uploading.setVisibility(View.INVISIBLE);        // Sets the progress bar to be invisible

        Log.i("Firebase","Started to UpLoadData");      // Logs to Console

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);     // Get a connection status from the system
        final NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);     // Gets the connection status with respect to wifi

        final String DeviceID = PreferenceDeviceID + "_";        // Sets the device ID to what it is in preferences
        final String path = PreferenceDeploymentID + "/" + PreferenceRole + "/";      // Gets the deployment ID and the role of the watch.

        upload.setOnClickListener(new View.OnClickListener()        // Waits for the upload button to be clicked
        {
            @Override
            public void onClick(View v)     // When the upload button is clicked
            {
                if (wifi.isConnected())     // It checks if wifi is connected
                {
                    uploading.setVisibility(View.VISIBLE);      // It then sets the upload button to be visible

                    String [] fileName =        // These are all the files that we want to upload to firebase
                    {
                        System,
                        Battery,
                        Estimote,
                        Pain_Results,
                        Pain_Activity,
                        EndOfDay_Results,
                        EndOfDay_Activity,
                        Followup_Results,
                        Followup_Activity,
                        Pedometer,
                        Heart_Rate,
                        Accelerometer
                    };

                    String [] type_ =       // These are the directory we want them to uplaod to <------------------------------  NOTE: THE ORDER CORRESPONDS TO THE FILE ORDER ABOVE
                    {
                        "SystemLog",
                        "SystemLog",
                        "EstimoteData",
                        "EMAResponses",
                        "EMAActivity",
                        "EMAResponses",
                        "EMAActivity",
                        "EMAResponses",
                        "EMAActivity",
                        "PedometerData",
                        "HeartRateData",
                        "AccelerometerData"
                    };

                    for(int i = 0; i < fileName.length; i++)        // For every file and directory listed above
                    {
                        storage = FirebaseStorage.getInstance("gs://besi-c-watchapp.appspot.com/");     // Get the storage from the site
                        storageRef = storage.getReference();       // Get a reference to the storage spot.
                        done = false;       // Set done to false to begin
                        final String file = DeviceID + fileName[i];     // The file is a combination of the DeviceID and the filename in the directory you are in now above.
                        final String remotePath = path+type_[i]+"/";      // Gets a path separated by the "/"
                        Log.i("Firebase","Uploading: "+file+" to: " + remotePath);      // Logs to Console

                        final Thread uploaderThread = new Thread()      // Starts a thread to upload the files
                        {
                            @Override
                            public void run()       // Runs this on upload continiously
                            {
                                try     // Tries to do this
                                {
                                    Log.i("Upload","File Uploading");      // Logs to Console

                                    UploadFile(remotePath,file,timeStamp);      // Sets the files to be uploaded
                                }
                                catch (Exception ex)        // If not
                                {
                                    Log.i("Upload","File does not exist");      // Logs to Console
                                }
                            }
                        };

                        uploaderThread.run();       // The thread is called to run
                        uploading.setVisibility(View.INVISIBLE);        // The upload button is set to invisible
                    }

                    finish();       // The Upload is finished.
                }

                else        // If there is no internet
                {
                    String data =  ("Firebase Service," + "Tried to Upload Files without Internet at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                    datalog.LogData();      // Saves the data into the directory.

                    uploading.setVisibility(View.INVISIBLE);        // Set the uploading to invisible
                    Toast("No Internet Connection");        // Tell us that there is no internet
                    Log.i("Firebase","Internet is Not Connected");      // Log that wifi is not connected.
                    finish();       // Finish the service.
                }
            }
        });

        setAmbientEnabled();        // Starts ambient mode
        upload.performClick();      // Upload button is clicked when created.
    }



    void UploadFile(String remotePath , String fileName, String timeStamp)      // This is the code that runs the upload file system
    {
        Uri file = Uri.fromFile(new File(localDirPath+fileName));       // Gets a path to the directory or makes a new directory
        StorageReference riversRef = storageRef.child(remotePath+timeStamp+"_"+file.getLastPathSegment());      // Gets into the reference directory
        UploadTask uploadTask = riversRef.putFile(file);        // Puts the file in the directory

        uploadTask.addOnFailureListener(new OnFailureListener()         // Register observers to listen for when the download is done or if it fails
        {
            @Override
            public void onFailure(@NonNull Exception exception)         // If it fails
            {
                String data =  ("Firebase Service," + "Uploading Files failed at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                Log.i("Firebase","Upload Failed");      // Logs to Console
                uploading.setVisibility(View.INVISIBLE);        // Set the visibility to invisible
                Toast("Upload Failed");        // Toast that the upload failed
                done = true;        // Set done to true
                succeed = true;     // Set succeed to true
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()        // Checks if the upload was successful
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)         // If it succeded
            {
                String data =  ("Firebase Service," + "Uploading Files Succeded at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
                datalog.LogData();      // Saves the data into the directory.

                Log.i("Firebase","Uploaded Successfully");      // Logs to Console
                Log.i("Firebase","Bytes Delivered: "+String.valueOf(taskSnapshot.getBytesTransferred()));     // Log the file size
                uploading.setVisibility(View.INVISIBLE);    // Set the uplaod to invisible
                Toast("Upload Successful");     // Tell us it was successful
                done = true;        // Set done to true
                succeed = false;        // Set success to false.
            }
        });
    }

    private void Toast(CharSequence text)     // This is a little charging toast notification.
    {
        Context context = getApplicationContext();      // Gets a context from the system.
        CharSequence textShown = (String) text;       // Pop up information to the person
        int duration = Toast.LENGTH_LONG;      // Shows the toast only for a short amount of time.
        Toast toast = Toast.makeText(context, textShown, duration);          // A short message at the end to say thank you.
        View view = toast.getView();        // Gets the view from the toast maker
        TextView textSeen = view.findViewById(android.R.id.message);        // Finds the text being used
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);        // Sets the toast to show up at the center of the screen
        textSeen.setTextColor(Color.WHITE);     // Changes the color of the text
        toast.show();       // Shows the toast.
    }

    @Override
    public void onDestroy()     // When the activity is ended
    {
        String data =  ("Firebase Service," + "is killed at," + SystemInformation.getTimeStamp());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger(Subdirectory_DeviceLogs, Sensors, data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.

        Log.i("Firebase","Activity Destroyed");      // Logs to Console

        wakeLock.release();     // Release the wakelock
        super.onDestroy();      // Destroy the activity.
    }
}
