package ru.ekbapp.intexartwoker.Module;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logs {
    public static void addLog(Context context, String text){
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
        SimpleDateFormat format_full = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
        Date now = new Date();
        String fileName = formatter.format(now) + ".txt";
        String date_create= format_full.format(now);

        Log.e("PROTOCOL",text);
        try
        {
            File root = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "intexart"+
                    File.separator +"logs");
            if (!root.exists())
            {
                root.mkdirs();
            }
            File gpxfile = new File(root, fileName);


            FileWriter writer = new FileWriter(gpxfile,true);
            writer.append(date_create+ "  -  "+text+"\n\n");
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }
}
