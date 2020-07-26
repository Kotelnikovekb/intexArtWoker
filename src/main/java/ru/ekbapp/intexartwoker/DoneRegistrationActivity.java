package ru.ekbapp.intexartwoker;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ekbapp.intexartwoker.Module.Logs;
import ru.ekbapp.intexartwoker.Views.DrawView;

import static ru.ekbapp.intexartwoker.StartUpActivity.SESSION_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.SETTINGS_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.URL_PORTAL_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.USER_NAME_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.USER_SIGNATURE_DEVICE_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.USER_SIGNATURE_S;

public class DoneRegistrationActivity extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    SharedPreferences sharedPreferences;
    TextView info;
    DrawView paintView;
    CardView signatureCard;
    Button clear,save;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_registration);
        sharedPreferences=getSharedPreferences(SETTINGS_S,MODE_PRIVATE);
        final TextView title=findViewById(R.id.textView2);
        info=findViewById(R.id.textView6);
        clear=findViewById(R.id.button5);
        signatureCard=findViewById(R.id.cardSignature);
        save=findViewById(R.id.button6);
        title.setText("Привет "+sharedPreferences.getString(USER_NAME_S,""));
        Animation connectingAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.login_title_loop);
        title.startAnimation(connectingAnimation);

        paintView=findViewById(R.id.printView);

        info.setText("Проверяю разрешения");
        if(checkAndRequestPermissions()) {
            checkSignature();
        }else {
            checkSignature();
        }

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clear();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation startAnimation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.done_downe);
                signatureCard.setAnimation(startAnimation);

                startAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        signatureCard.setVisibility(View.GONE);
                        info.setText("Сохраняю файл");
                        int permissionWrite = ContextCompat.checkSelfPermission(DoneRegistrationActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        int permissionRead = ContextCompat.checkSelfPermission(DoneRegistrationActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (permissionWrite==PackageManager.PERMISSION_GRANTED&&permissionRead==PackageManager.PERMISSION_GRANTED){
                            try{
                                File root = new File(Environment.getExternalStorageDirectory() +
                                        File.separator + getResources().getString(R.string.folder_name)+
                                        File.separator+getResources().getString(R.string.foler_signature_user));
                                if (!root.exists())
                                {
                                    Log.e("PERMISSION","Папки не существует!");
                                    Log.e("PERMISSION","Создаю папку "+root.mkdirs());

                                }

                                file = new File(root,    sharedPreferences.getString(SESSION_S,"")+".png");
                                FileOutputStream ostream = new FileOutputStream(file);

                                Bitmap well = paintView.getBitmap();
                                Bitmap save = Bitmap.createBitmap(320, 480,
                                        Bitmap.Config.ARGB_8888);
                                Paint paint = new Paint();
                                paint.setColor(Color.WHITE);
                                Canvas now = new Canvas(save);
                                now.drawRect(new Rect(0, 0, 320, 480), paint);
                                now.drawBitmap(well,
                                        new Rect(0, 0, well.getWidth(), well.getHeight()),
                                        new Rect(0, 0, 320, 480), null);

                                if (save == null) {
                                }
                                save.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                new UploadFileAsync().execute(file.toString());


                            }catch (FileNotFoundException e){
                                e.printStackTrace();
                                Log.e("PERMISSION",e.toString());
                                Snackbar.make(save,"Ошибка "+e.getMessage(),Snackbar.LENGTH_LONG).show();
                            }
                        }else {
                            Snackbar.make(save,"Нет доступа к хранилищу. Подпись не сохранена.",Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });


    }
    private  boolean checkAndRequestPermissions() {
        int permissionRead = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWrite = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int RecordPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionRead != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (RecordPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("TAG", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            &&perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            &&perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                        Log.d("TAG", "sms & location services permission granted");

                        createFolder();
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("TAG", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("Для работы приложения необходимы разрешения",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:

                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {

                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
        createFolder();

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    void createFolder(){
        info.setText("Проверяю папки");
        int permissionWrite = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionWrite == PackageManager.PERMISSION_GRANTED){
            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + getResources().getString(R.string.folder_name));
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {
                Logs.addLog(this,"папка компании доступна");
            } else {
                Logs.addLog(this,"папка компании не доступна");
            }
        }
        File folder_log = new File(Environment.getExternalStorageDirectory() +
                File.separator + getResources().getString(R.string.folder_name)+
                File.separator +getResources().getString(R.string.folder_log));
        boolean success_log = true;
        if (!folder_log.exists()) {
            success_log = folder_log.mkdirs();
        }
        if (success_log) {
            Logs.addLog(this,"папка log  доступна");
        } else {
            Logs.addLog(this,"папка log не доступна");
            success_log = folder_log.mkdirs();
        }
        File folder_zip = new File(Environment.getExternalStorageDirectory() +
                File.separator + getResources().getString(R.string.folder_name)+
                File.separator +getResources().getString(R.string.folder_zip));
        boolean success_zip = true;
        if (!folder_zip.exists()) {
            success_zip = folder_zip.mkdirs();
        }
        if (success_zip) {
            Logs.addLog(this,"папка zip  доступна");
        } else {
            Logs.addLog(this,"папка zip не доступна");
            success_zip = folder_zip.mkdirs();
        }

        File folder_request = new File(Environment.getExternalStorageDirectory() +
                File.separator + getResources().getString(R.string.folder_name)+
                File.separator +getResources().getString(R.string.folder_request));
        boolean success_request = true;
        if (!folder_request.exists()) {
            success_request = folder_request.mkdirs();
        }
        if (success_request) {
            Logs.addLog(this,"папка request  доступна");
        } else {
            Logs.addLog(this,"папка request не доступна");
            success_request = folder_request.mkdirs();
        }



        checkSignature();
    }
    void checkSignature(){
        info.setText("Проверяю подпись");
        String signature=sharedPreferences.getString(USER_SIGNATURE_S,"");

        if (signature.equals("")){
            info.setText("Создайте шаблон подписи");

            signatureCard.setVisibility(View.VISIBLE);
            Animation startAnimation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.login_start);
            signatureCard.setAnimation(startAnimation);
        }else{
            if (sharedPreferences.getString(USER_SIGNATURE_DEVICE_S,"").equals("")){
                //TODO добавить загрузку подписи
                info.setText("Создайте шаблон подписи");

                signatureCard.setVisibility(View.VISIBLE);
                Animation startAnimation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.login_start);
                signatureCard.setAnimation(startAnimation);
            }else {
                startActivity(new Intent(DoneRegistrationActivity.this,MainActivity.class));
                finish();
            }
        }

    }

    private class UploadFileAsync extends AsyncTask<String, Void, String> {
        String answer="";
        @Override
        protected String doInBackground(String... params) {

            try {
                String sourceFileUri = params[0];

                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile = new File(sourceFileUri);

                if (sourceFile.isFile()) {

                    try {
                        String upLoadServerUri = sharedPreferences.getString(URL_PORTAL_S,"")
                                +"/Login/saveSignature.php";

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(upLoadServerUri);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE",
                                "multipart/form-data");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("bill", sourceFileUri);




                        dos = new DataOutputStream(conn.getOutputStream());


                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""
                                + sourceFileUri + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);


                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);

                        // Responses from the server (code and message)
                        int serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn
                                .getResponseMessage();

                        if (serverResponseCode == 200) {
                            BufferedReader br = null;

                            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String strCurrentLine;
                            while ((strCurrentLine = br.readLine()) != null) {
                                answer=strCurrentLine;
                            }




                            //Toast.makeText(ctx, "File Upload Complete.",
                            //      Toast.LENGTH_SHORT).show();

                            // recursiveDelete(mDirectory1);

                        }

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                    } catch (Exception e) {

                        // dialog.dismiss();
                        e.printStackTrace();

                    }
                    // dialog.dismiss();

                } // End else block


            } catch (Exception ex) {
                // dialog.dismiss();

                ex.printStackTrace();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject a=new JSONObject(result);
                if (a.getString("success").equals("1")){
                    info.setText("Подпись успешно отправили");
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(USER_SIGNATURE_S,a.getString("link"));
                    editor.putString(USER_SIGNATURE_DEVICE_S,file.toString());
                    editor.commit();
                    startActivity(new Intent(DoneRegistrationActivity.this,MainActivity.class));
                    finish();
                }else{
                    info.setText("Ошибка отправки подписи");
                    startActivity(new Intent(DoneRegistrationActivity.this,MainActivity.class));
                    finish();
                }

            }catch (JSONException er){
                er.printStackTrace();
                info.setText("Ошибка отправки подписи");
                startActivity(new Intent(DoneRegistrationActivity.this,MainActivity.class));
                finish();

            }


        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}
