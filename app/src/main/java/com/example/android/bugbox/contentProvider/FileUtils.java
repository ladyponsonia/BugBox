package com.example.android.bugbox.contentProvider;

/* with help from https://developer.android.com/training/data-storage/files and
https://stackoverflow.com/questions/14376807/how-to-read-write-string-from-a-file-in-android
 */

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtils {

    private void writeToFile(String data, Context context, String filename) {

        File file = new File(context.getFilesDir(), filename);
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readFromFile(Context context, String filename) {
       String fileContent = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int size = inputStream.available();
                char[] buffer = new char[size];

                inputStreamReader.read(buffer);

                inputStream.close();
                fileContent = new String(buffer);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return fileContent;
    }
}
