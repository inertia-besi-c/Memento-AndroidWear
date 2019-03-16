package com.linklab.emmanuelogunjirin.besi_c;

// Imports
import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

@SuppressWarnings("ALL")
public class DataLogger     // A function that runs the data logging data
{
    private String FileName, Content;        // Variable names for the file characters and contents.

    public DataLogger(String filename ,String content)      // This just includes all the variable for the data logger function
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

    public boolean isExternalStorageReadable()    /* Checks if external storage is available to at least read */
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
                @SuppressLint("SdCardPath")
                File BESI_directory = new File("/sdcard/BESI_C/");    // Path to file in the storage of the device

                if (BESI_directory.isDirectory())
                {
                    // Do nothing
                }
                else
                {
                    BESI_directory.mkdirs();
                }

                File myFile = new File("/sdcard/BESI_C/"+FileName);     // Adds the filename to the path of the file
                myFile.createNewFile();
                FileOutputStream fileOut = new FileOutputStream(myFile,true);
                OutputStreamWriter myOutWriter =new OutputStreamWriter(fileOut);
                myOutWriter.append(Content+"\n");
                myOutWriter.close();
                fileOut.close();
            }
            catch (IOException e)       // If it does not write the file, imform us it failed.
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
            Log.i("Error","Failed to write to directory");      // If it could not make the directory, tell us it failed.
        }
    }

    public void WriteData()
    {
        if (isExternalStorageWritable())
        {
            try
            {
                @SuppressLint("SdCardPath")
                File BESI_directory = new File("/sdcard/BESI_C/");    // Path to file in the storage of the device

                if (BESI_directory.isDirectory())
                {
                    // Do nothing
                }
                else
                {
                    BESI_directory.mkdirs();
                }

                File myFile = new File("/sdcard/BESI_C/"+FileName);     // Adds the filename to the path of the file
                myFile.createNewFile();
                FileOutputStream fileOut = new FileOutputStream(myFile,false);
                OutputStreamWriter myOutWriter =new OutputStreamWriter(fileOut);
                myOutWriter.write(Content);
                myOutWriter.close();
                fileOut.close();
            }
            catch (IOException e)       // If it does not write the file, imform us it failed.
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
            Log.i("Error","Failed to write to directory");      // If it could not make the directory, tell us it failed.
        }
    }

    public String ReadData()
    {
        StringBuilder text = new StringBuilder();
        try
        {
            File file = new File("/sdcard/BESI_C/",FileName);
            BufferedReader bufferedReaderr = new BufferedReader(new FileReader(file));
            String line;

            while ((line = bufferedReaderr.readLine()) != null)
            {
                text.append(line);
                text.append('\n');
            }
            bufferedReaderr.close() ;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return text.toString();
    }
}


