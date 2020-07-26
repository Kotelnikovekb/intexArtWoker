package ru.ekbapp.intexartwoker.Module;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import ru.ekbapp.intexartwoker.MainActivity;
import ru.ekbapp.intexartwoker.R;

import static android.app.PendingIntent.getActivity;


public class Share_void {
    public static SimpleDateFormat SQL_FORMAT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd.MM.yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    public static String parseDateToddMMyyyyHHmm(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd.MM.yyyy HH:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    public static String parseDateToHHmm(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "HH:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    static String parseTime(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "HH:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    public static String readFile(File myExternalFile)
    {
        String myData = "";
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
            }
            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myData;
    }
    public static String writeFile(JSONObject title,File file){
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();

            String jsonStr = title.toString();
            FileWriter writer1 = new FileWriter(file, true);
            writer1.append(jsonStr);
            writer1.flush();
            writer1.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return "";
    }
    public static File createFileFromInputStream(InputStream inputStream, String fileName) {

        try{
            File f = new File(fileName);
            f.setWritable(true, false);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            System.out.println("error in creating a file");
            e.printStackTrace();
        }

        return null;

    }
    public static boolean setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();

            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int)px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;

        } else {
            return false;
        }

    }
    public static String zipFolder(Context context, String inputFolderPath, String outZipPath) {
        String answer="0";
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
            answer="1";
        } catch (IOException ioe) {
            Log.e("", ioe.getMessage());
            createNotification(context,"Ошибка создания архива",ioe.toString());

        }
        return answer;
    }
    public static String sendZip(final Context context, final String requestURL, final File uploadFile, final String Username, final String id){
        final String charset = "UTF-8";
        final String[] answer = new String[1];
        Log.e("jsonA","start");
        int version = 0;
        try {
            PackageInfo packageInfo=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version=packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final int finalVersion = version;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                    multipart.addHeaderField("User-Agent", "CodeJava");
                    multipart.addHeaderField("Test-Header", "Header-Value");

                    multipart.addFormField("Username", Username);
                    multipart.addFormField("id", id);
                    multipart.addFormField("version", finalVersion+"");

                    multipart.addFilePart("uploaded_file[]", uploadFile);

                    String response = multipart.finish();
                    answer[0]=response;
                    Log.e("jsonA",response);
                    JSONObject obj=new JSONObject(response);

                    try{
                        if(obj.getString("success").equals("1")){
                            createNotification(context,"ЖК","Отчет по заявке "+id+" создан");
                        }else{
                            createNotification(context,"ЖК","Ошибка отправки");

                        }

                    }catch (JSONException er){
                        er.printStackTrace();
                        createNotification(context,"Ошибка отправка отчета",er.toString());
                    }


                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.e("jsonA",ex.toString());
                    createNotification(context,"Ошибка отправка отчета",ex.toString());
                    answer[0] = "";

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jsonA",e.toString());
                    createNotification(context,"Ошибка отправка отчета",e.toString());
                    answer[0] = "";
                }
            }
        }).start();
        return answer[0];
    }
    static void createNotification(Context context, String title, String text){
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME1",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION1");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title).setSound(soundUri)
                .setContentText(text)// message for notification
                .setAutoCancel(true);

        Intent intent = new Intent(context, MainActivity.class).putExtra("s","s");


        PendingIntent pi = getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
