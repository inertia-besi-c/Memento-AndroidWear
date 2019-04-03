package com.linklab.INERTIA.besi_c;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FireBaseDataUpload extends Service {
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://besi-c-watchapp.appspot.com/");
    StorageReference storageRef = storage.getReference();
    String localDirPath = "sdcard/BESI_C/";


    public FireBaseDataUpload() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)    /* Establishes the sensor and the ability to collect data at the start of the data collection */
    {


        return START_REDELIVER_INTENT;
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
                stopSelf();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("Upload","Successful");
                Log.i("Upload","Bytes Delivered: "+String.valueOf(taskSnapshot.getBytesTransferred()));
                stopSelf();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }


    @Override
    public void onDestroy()
    {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
