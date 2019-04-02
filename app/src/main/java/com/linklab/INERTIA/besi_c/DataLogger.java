package com.linklab.INERTIA.besi_c;

// Imports

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

// src: https://developer.android.com/training/data-storage/files.html#WriteExternalStorage
@SuppressWarnings("ALL")    // Service wide suppression for the data logger names.
public class DataLogger     // A function that runs the data logging data
{
    private String FileName, Content;        // Variable names for the file characters and contents.

    public DataLogger(String filename ,String content)      // This just includes all the variable for the data logger function
    {
        FileName = filename;        // Initiates a variable for the filename
        Content = content;      // Initiates a variable for the content of the file name
    }

    private boolean isExternalStorageWritable()     /* Checks if external storage is available for read and write */
    {
        String state = android.os.Environment.getExternalStorageState();        // Checks if the sdcard can be written to.
        return android.os.Environment.MEDIA_MOUNTED.equals(state);      // Returns the state of the sdcard.
    }

    public boolean isExternalStorageReadable()    /* Checks if external storage is available to at least read */
    {
        String state = android.os.Environment.getExternalStorageState();        // Checks if the sdcard can be read from
        return android.os.Environment.MEDIA_MOUNTED.equals(state) || android.os.Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);      // Returns the state of the sdcard.
    }

    public void LogData()       // This is what is run when logdata is called.
    {
        if (isExternalStorageWritable())        // Checks if the storage is writable
        {
            try
            {
                @SuppressLint("SdCardPath")     // Suppresses the path name.
                File BESI_directory = new File("/sdcard/BESI_C/");    // Path to file in the storage of the device

                if (BESI_directory.isDirectory())       // If there is a directory with that name
                {
                    // Do nothing
                }
                else    // If there is no directory with that name
                {
                    BESI_directory.mkdirs();        // Make a directory with the name.
                }

                File myFile = new File("/sdcard/BESI_C/"+FileName);     // Adds the filename to the path of the file
                myFile.createNewFile();     // Cretates the new file
                FileOutputStream fileOut = new FileOutputStream(myFile,true);       // This is what the file outputs.
                OutputStreamWriter myOutWriter =new OutputStreamWriter(fileOut);        // Enters the new line in the file
                myOutWriter.append(Content+"\n");       // Appends the content to the file
                myOutWriter.close();        // Closes the file
                fileOut.close();        // Closes the directory.
            }
            catch (IOException e)       // If it does not write the file, imform us it failed.
            {
                // Do nothing.
            }
            catch (Exception ex)
            {
                // Do nothing.
            }
        }

        else        // If we canot make the directory
        {
            // Do nothing.
        }
    }

    public void WriteData()     // This writes the data to the sdcard.
    {
        if (isExternalStorageWritable())        // Checks if we can write data to the card.
        {
            try
            {
                @SuppressLint("SdCardPath")     // Suppresses the sdcard image name.
                File BESI_directory = new File("/sdcard/BESI_C/");    // Path to file in the storage of the device

                if (BESI_directory.isDirectory())       // If there is a directory with the name
                {
                    // Do nothing
                }
                else        // If there is no directory with the name
                {
                    BESI_directory.mkdirs();        // Do nothing.
                }

                File myFile = new File("/sdcard/BESI_C/"+FileName);     // Adds the filename to the path of the file
                myFile.createNewFile();     // Cretates the new file
                FileOutputStream fileOut = new FileOutputStream(myFile,false);       // This is what the file outputs.
                OutputStreamWriter myOutWriter =new OutputStreamWriter(fileOut);        // Enters the new line in the file
                myOutWriter.write(Content);       // Appends the content to the file
                myOutWriter.close();        // Closes the file
                fileOut.close();        // Closes the directory.
            }
            catch (IOException e)       // If it does not write the file, imform us it failed.
            {
                // Do nothing.
            }
            catch (Exception ex)
            {
                // Do nothing.
            }
        }

        else        // If we canot make the directory
        {
          // Do nothing.
        }
    }

    public String ReadData()    // This reads the data from the sdcard
    {
        StringBuilder text = new StringBuilder();       // This is the new string that is built
        try     // Tires to run the following.
        {
            File file = new File("/sdcard/BESI_C/",FileName);       // Creates a filename with the new filename
            BufferedReader bufferedReaderr = new BufferedReader(new FileReader(file));      // Reads the buffer in the system
            String line;        // Creates a new line.

            while ((line = bufferedReaderr.readLine()) != null)     // While the line is not blank
            {
                text.append(line);      // Append the text to the line
                text.append('\n');      // Start a new line.
            }
            bufferedReaderr.close() ;       // Close the buffer reader.
        }
        catch (IOException e)   // Catch statement
        {
            e.printStackTrace();        // Ignore this.
        }
        return text.toString();     // Return the text to the string.
    }

    public static boolean writeToFile(Date time1, String data)      /* Special way to log data for the estimote.. (This was moved from Jamie's File and was just used) PLEASE DO NOT REMOVE */
    {
        boolean flag = true;
        String path = Environment.getExternalStorageDirectory() + "/BESI_C";
        String fileName = "Estimote_Data.csv";
        File file = new File(path);

        if (file.exists() == false)
        {
            file.mkdirs();
        }

        String filepath = path + "/" + fileName;

        try
        {
            FileOutputStream fStream = new FileOutputStream(filepath, true);
            fStream.write(data.getBytes());
        }
        catch (Exception ex)
        {
            flag = false;
        }

        return flag;
    }
}
