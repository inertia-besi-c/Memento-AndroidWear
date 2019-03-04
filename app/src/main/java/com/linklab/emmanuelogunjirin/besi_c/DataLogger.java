package com.linklab.emmanuelogunjirin.besi_c;

import android.annotation.SuppressLint;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@SuppressWarnings("ALL")
public class DataLogger
{
    private String FileName,Content;

    public DataLogger(String filename ,String content)
    {
        FileName = filename;
        Content = content;
    }

    // src: https://developer.android.com/training/data-storage/files.html#WriteExternalStorage
    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable()
    {
        String state = android.os.Environment.getExternalStorageState();
        return android.os.Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable()
    {
        String state = android.os.Environment.getExternalStorageState();
        return android.os.Environment.MEDIA_MOUNTED.equals(state) || android.os.Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public void LogData()
    {
        if (isExternalStorageWritable())
        {

            try
            {
                @SuppressLint("SdCardPath") File BESI_dir = new File("/sdcard/BESI_C/");    // Path to file in the storage of the device
                if (BESI_dir.isDirectory()){} else {BESI_dir.mkdirs();}
                File myFile = new File("/sdcard/BESI_C/"+FileName);     // Adds the filename to the path of the file
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile,true);
                OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
                myOutWriter.append(Content+"\n");
                myOutWriter.close();
                fOut.close();
            }

            catch (IOException e)       // If it does not write the file, tell us it failed.
            {
                Log.i("Error",e.toString());
                Log.i("Error","Failed to write file");
            }

            catch (Exception ex)
            {
                Log.i("Error",ex.toString());
            }

        }

        else
        {
            Log.i("Error","Failed to write to directory");
        }
    }
}


