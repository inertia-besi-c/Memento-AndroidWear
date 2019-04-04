package com.linklab.INERTIA.besi_c;

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
public class FireBase_Upload extends WearableActivity
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
    protected void onCreate(Bundle savedInstanceState) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);     // Power manager calls the power distribution service.
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "End of Day EMA:wakeLock");        // It initiates a full wakelock to turn on the screen.
        wakeLock.acquire();      // The screen turns off after the timeout is passed.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base__upload);

        upload = findViewById(R.id.upload);
        uploading = findViewById(R.id.progressBar);

        upload.setVisibility(View.INVISIBLE);
        uploading.setVisibility(View.INVISIBLE);

        Log.i("Upload","onStartCommand upLoadData");
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        Preferences pref = new Preferences();
        final String timeStamp = new SystemInformation().getFolderTimeStamp();

        final String DeviceID = pref.DeviceID + "_";
        final String path = pref.DeploymentID + "/" + pref.Role + "/";

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (wifi.isConnected())
                {
                    uploading.setVisibility(View.VISIBLE);

                    String [] fileName =
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

                    String [] type_ =
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

                    for(int i = 0; i < fileName.length; i++)
                    {
                        storage = FirebaseStorage.getInstance("gs://besi-c-watchapp.appspot.com/");
                        storageRef = storage.getReference();

                        done = false;
                        final String file = DeviceID + fileName[i];
                        final String remotePath = path+type_[i]+"/"+timeStamp+"/";

                        Log.i("Upload","Uploading: "+file+" to: " + remotePath);

                        final Thread uploaderThread = new Thread(){
                            @Override
                            public void run() {
                                try{
                                UploadFile(remotePath,file);}
                                catch (Exception ex){Log.i("Upload","File does not exist");}

                            }
                        };

                        uploaderThread.run();

                        uploading.setVisibility(View.INVISIBLE);

                    }
                    finish();
                }
                else
                {
                    uploading.setVisibility(View.INVISIBLE);
                    Toast("No Wifi");
                    Log.i("Upload","Wif Not Connected");
                    finish();
                }
            }
        });

        setAmbientEnabled();
        upload.performClick();
    }



    void UploadFile(String remotePath , String fileName)
    {
        Uri file = Uri.fromFile(new File(localDirPath+fileName));
        StorageReference riversRef = storageRef.child(remotePath+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("Upload","Failed");
                uploading.setVisibility(View.INVISIBLE);
                Toast("Failed");
                done = true;
                succeed = true;

                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("Upload","Successful");
                Log.i("Upload","Bytes Delivered: "+String.valueOf(taskSnapshot.getBytesTransferred()));
                uploading.setVisibility(View.INVISIBLE);
                Toast("Uploaded");
                done = true;
                succeed = false;
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    private void Toast(CharSequence text)     // This is a little charging toast notification.
    {
        Context context = getApplicationContext();      // Gets a context from the system.
        int duration = Toast.LENGTH_LONG;      // Shows the toast only for a short amount of time.
        Toast toast = Toast.makeText(context, text, duration);          // A short message at the end to say thank you.
        toast.show();       // Shows the toast.
    }

    @Override
    public void onDestroy(){
        wakeLock.release();
        super.onDestroy();
    }

}
