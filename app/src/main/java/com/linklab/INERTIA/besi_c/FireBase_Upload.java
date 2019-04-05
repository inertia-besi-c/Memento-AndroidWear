package com.linklab.INERTIA.besi_c;

// Imports
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
    FirebaseStorage storage;        // The storage on firebase
    StorageReference storageRef;        // The storage reference on firbase
    PowerManager.WakeLock wakeLock;     // Wakelock
    String localDirPath = new Preferences().Directory;     // The directory path on the watch

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        String data =  ("Firebase Started at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
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

        Preferences pref = new Preferences();       // Creates a new preference folder
        final String timeStamp = new SystemInformation().getFolderTimeStamp();      // Gets a time stamp from System information
        final String DeviceID = pref.DeviceID + "_";        // Sets the device ID to what it is in preferences
        final String path = pref.DeploymentID + "/" + pref.Role + "/";      // Gets the deployment ID and the role of the watch.

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
                            "System_Activity.csv",
                            "Battery_Activity.csv",
                            "Estimote_Data.csv",
                            "Pain_EMA_Results.csv",
                            "Pain_EMA_Activity.csv",
                            "EndOfDay_EMA_Results.csv",
                            "Followup_EMA_Results.csv",
                            "Followup_EMA_Activity.csv",
                            "Pedometer_Data.csv",
                            "Heart_Rate_Data.csv"
                    };

                    String [] type_ =       // These are the directory we want them to uplaod to <------------------------------  NOTE: THE ORDER CORRESPONDS TO THE FILE ORDER ABOVE
                    {
                            "SystemLog",
                            "SystemLog",
                            "EstimoteData",
                            "EMAResponses",
                            "EMAActivity",
                            "EMAResponses",
                            "EMAResponses",
                            "EMAActivity",
                            "PedometerData",
                            "HeartRateData"
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
                            public void run() {
                                try{
                                UploadFile(remotePath,file,timeStamp);}
                                catch (Exception ex){Log.i("Upload","File does not exist");}
                            }
                        };

                        uploaderThread.run();       // The thread is called to run
                        uploading.setVisibility(View.INVISIBLE);        // The upload button is set to invisible
                    }

                    finish();       // The Upload is finished.
                }

                else        // If there is no internet
                {
                    String data =  ("Tried to Upload Files without Internet at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                    DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
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



    void UploadFile(String remotePath , String fileName, String timeStamp)
    {
        Uri file = Uri.fromFile(new File(localDirPath+fileName));
        StorageReference riversRef = storageRef.child(remotePath+timeStamp+"_"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener()         // Register observers to listen for when the download is done or if it fails
        {
            @Override
            public void onFailure(@NonNull Exception exception)         // If it fails
            {
                String data =  ("Uploading Files failed at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
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
                String data =  ("Uploading Files Succeded at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
                DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
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
        int duration = Toast.LENGTH_LONG;      // Shows the toast only for a Long amount of time.
        Toast toast = Toast.makeText(context, text, duration);          // A short message at the end to say thank you.
        toast.show();       // Shows the toast.
    }

    @Override
    public void onDestroy()     // When the activity is ended
    {
        String data =  ("Firebase is killed at " + new SystemInformation().getTimeStamp());       // This is the format it is logged at.
        DataLogger datalog = new DataLogger("Sensor_Activity.csv",data);      // Logs it into a file called System Activity.
        datalog.LogData();      // Saves the data into the directory.

        Log.i("Firebase","Activity Destroyed");      // Logs to Console

        wakeLock.release();     // Release the wakelock
        super.onDestroy();      // Destroy the activity.
    }

}
