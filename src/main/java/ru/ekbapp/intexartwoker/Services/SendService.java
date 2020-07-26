package ru.ekbapp.intexartwoker.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ru.ekbapp.intexartwoker.MainActivity;
import ru.ekbapp.intexartwoker.Module.DatabaseHelper;
import ru.ekbapp.intexartwoker.Module.MultipartUtility;
import ru.ekbapp.intexartwoker.R;

import static android.app.PendingIntent.getActivity;
import static ru.ekbapp.intexartwoker.StartUpActivity.SESSION_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.SETTINGS_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.URL_PORTAL_S;

public class SendService extends Service {
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    String req,TAG_S="TAG_S";
    SharedPreferences sharedPreferences;
    public SendService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences=getSharedPreferences(SETTINGS_S,MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(getApplicationContext());

        db = databaseHelper.open();


        //geo();
        Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG_S,"запуск проверки");
                Log.e(TAG_S,"Поиск документов");

                try {
                    userCursor= db.rawQuery("select * from " + DatabaseHelper.COLUMN_NAME + " where " +
                            DatabaseHelper.COLUMN_NEW_REQ_STATUS + "=?", new String[]{String.valueOf(0)});
                    if(userCursor!=null&&userCursor.moveToFirst()){
                        userCursor.moveToFirst();
                        String nomer=userCursor.getString(1);
                        req=userCursor.getString(3);
                        Log.e("sendServise",nomer);
                        String zip_file= Environment.getExternalStorageDirectory() +
                                File.separator + getResources().getString(R.string.folder_name)+
                                File.separator +getResources().getString(R.string.folder_zip)+
                                File.separator+nomer+".zip";
                        zipFolder(Environment.getExternalStorageDirectory() +
                                File.separator + getResources().getString(R.string.folder_name)+
                                File.separator +getResources().getString(R.string.folder_request)+
                                File.separator+nomer,zip_file);
                        String a= sendZip(sharedPreferences.getString(URL_PORTAL_S,"")+"/Request/closeRequest.php",
                                new File(zip_file),req,nomer);



                    }

                }catch (NullPointerException e) {
                    e.printStackTrace();
                    Log.e("sendServise","ошибка "+e.getMessage());
                }catch (SQLException e){
                    e.printStackTrace();
                    Log.e("sendServise","ошибка "+e.getMessage());
                }
                finally {
                    Log.e("sendServise","Больше нет записей");

                }

            }

        };

        timer.schedule(timerTask,0,60000);







        return super.onStartCommand(intent, flags, startId);
    }
    String sendZip(final String requestURL, final File uploadFile, final String id, final String number){
        final String charset = "UTF-8";
        final String[] answer = new String[1];
        Log.e("jsonA","start");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                    multipart.addHeaderField("User-Agent", "CodeJava");
                    multipart.addHeaderField("Test-Header", "Header-Value");

                    multipart.addFormField("session", sharedPreferences.getString(SESSION_S,""));
                    multipart.addFormField("id", id);

                    multipart.addFilePart("uploaded_file[]", uploadFile);

                    String response = multipart.finish();
                    Log.e("jsonA",response);
                    JSONObject obj=new JSONObject(response);

                    Log.e("jsonA",obj.getString("message"));
                    answer[0] = response;
                    if (obj.getString("success").equals("1")){
                        notifycation_send("Документы по заявке "+number+" отправлены!");
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseHelper.COLUMN_NEW_REQ_STATUS, 2);
                        db.update(DatabaseHelper.COLUMN_NAME, cv, "idreq" + "=" +id, null);
                        db.delete(DatabaseHelper.COLUMN_NAME, "idreq" + "=" +id, null);

                        Log.e("send_pdf","Документ отправлен ");
                    }else {
                        notifycation_send("Ошибка отправки документов. \n"+obj.getString("message"));
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseHelper.COLUMN_NEW_REQ_STATUS, 2);
                        db.update(DatabaseHelper.COLUMN_NAME, cv, "idreq" + "=" +id, null);
                        db.delete(DatabaseHelper.COLUMN_NAME, "idreq" + "=" +id, null);

                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.e("jsonA",ex.toString());
                    answer[0] = "";

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jsonA",e.toString());
                    answer[0] = "";
                }
            }
        }).start();
        return answer[0];
    }
    private  void zipFolder(String inputFolderPath, String outZipPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outZipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File srcFile = new File(inputFolderPath);
            File[] files = srcFile.listFiles();
            Log.d("", "Zip directory: " + srcFile.getName());
            for (int i = 0; i < files.length; i++) {
                Log.d("", "Adding file: " + files[i].getName());
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(files[i]);
                zos.putNextEntry(new ZipEntry(files[i].getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
        } catch (IOException ioe) {
            Log.e("", ioe.getMessage());
        }
    }
    public void notifycation_send(String text) {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME1",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION1");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle("ЖК").setSound(soundUri)
                .setContentText(text)// message for notification
                .setAutoCancel(true);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class).putExtra("s","s");


        PendingIntent pi = getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
