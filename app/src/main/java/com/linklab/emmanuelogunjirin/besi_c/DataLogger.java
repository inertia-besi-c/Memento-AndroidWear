package com.linklab.emmanuelogunjirin.besi_c;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DataLogger {

    private String FileName,Content;

    public DataLogger()
    {

    }
    public DataLogger(String filename ,String content)
    {
        FileName = filename;
        Content = content;
    }

//--------------------------------------------------------------------------------------------------
// src: https://developer.android.com/training/data-storage/files.html#WriteExternalStorage
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = android.os.Environment.getExternalStorageState();
        if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = android.os.Environment.getExternalStorageState();
        if (android.os.Environment.MEDIA_MOUNTED.equals(state) ||
                android.os.Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
//--------------------------------------------------------------------------------------------------
    public void LogData(){

        if (isExternalStorageWritable()){

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String timeStamp = dateFormat.format(date); //2016/11/16 12:08:43

        try {
            File Besi_dir = new File("/sdcard/BESI_C/");
            if (Besi_dir.isDirectory()){} else {Besi_dir.mkdirs();}
            File myFile = new File("/sdcard/BESI_C/"+FileName);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile,true);
            OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
            myOutWriter.append(Content+"\n");
            myOutWriter.close();
            fOut.close();
        } catch (IOException e){
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
                Log.i("Error","Failed to write to file");
            }

    }
}


