package ru.ekbapp.intexartwoker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import ru.ekbapp.intexartwoker.Module.DatabaseHelper;
import ru.ekbapp.intexartwoker.Module.JSONParser;

public class StartUpActivity extends AppCompatActivity {

    EditText password;
    MaskedEditText phone;
    Button login,loginSMS;
    ProgressBar progressBar;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    public final static String SETTINGS_S="SETTINGS_S";
    public final static String SESSION_S="SESSION_S";
    public final static String URL_PORTAL_S="URL_PORTAL_S";
    public final static String USER_NAME_S="USER_NAME_S";
    public final static String USER_ROLE_S="USER_ROLE_S";
    public final static String DOCUMENT_LIST_S="DOCUMENT_LIST_S";
    public final static String USER_SIGNATURE_S="USER_SIGNATURE_S";//файл на сервере
    public final static String USER_SIGNATURE_DEVICE_S="USER_SIGNATURE_DEVICE_S";//Файл на устройстве
    CardView Card;
    Animation startAnimation;
    SQLiteDatabase db;
    DatabaseHelper databaseHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.create_db();

        phone=findViewById(R.id.phone_LIA);
        password=findViewById(R.id.editText);

        login=findViewById(R.id.button2);
        loginSMS=findViewById(R.id.button3);
        progressBar=findViewById(R.id.progressBar3);
        Card=findViewById(R.id.cardLogin);

        final TextView title=findViewById(R.id.textView2);
        Animation titleAnimation=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.login_title);
        title.setAnimation(titleAnimation);

        sharedPreferences=getSharedPreferences(SETTINGS_S,MODE_PRIVATE);
        final String session=sharedPreferences.getString(SESSION_S,"");
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(URL_PORTAL_S,"https://intehart.ru/admin/Android/Woker");
        editor.commit();

        final String[] mToken = new String[1];
        titleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FirebaseInstanceId f=FirebaseInstanceId.getInstance();
                f.getInstanceId().addOnSuccessListener(StartUpActivity.this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        mToken[0] = instanceIdResult.getToken();
                        if (!session.equals("")){
                            title.clearAnimation();
                            Animation connectingAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.login_title_loop);
                            title.startAnimation(connectingAnimation);
                            new AttemptAutoLogin().execute(session,mToken[0]);

                        }else{
                            Card.setVisibility(View.VISIBLE);
                            startAnimation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.login_start);
                            Card.setAnimation(startAnimation);

                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText()!=null&&phone.getRawText().length()==10&&!password.getText().toString().equals("")){
                    new AttemptLoginIn().execute(phone.getRawText(),password.getText().toString(),mToken[0]);
                    login.setEnabled(false);
                    loginSMS.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                }else {
                    Snackbar.make(login,"Заполните поля", Snackbar.LENGTH_LONG).show();
                }
            }
        });



    }
    private class AttemptAutoLogin extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String token = args[1];
            String session = args[0];
            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("token", token));
            params.add(new BasicNameValuePair("session", session));


            JSONObject json = jsonParser.makeHttpRequest(sharedPreferences.getString(URL_PORTAL_S,"")
                    +"/Login/chekUser.php","POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            try {
                if (result != null) {
                    if (result.getString("success").equals("1")){
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString(DOCUMENT_LIST_S,result.getString("documentList"));
                        editor.commit();
                        startActivity(new Intent(StartUpActivity.this,DoneRegistrationActivity.class));
                    }else{
                        Card.setVisibility(View.VISIBLE);
                        startAnimation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.login_start);
                        Card.setAnimation(startAnimation);
                    }

                } else {
                    Card.setVisibility(View.VISIBLE);
                    startAnimation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.login_start);
                    Card.setAnimation(startAnimation);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("requst",e.toString());
                Card.setVisibility(View.VISIBLE);
                startAnimation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.login_start);
                Card.setAnimation(startAnimation);

            }



        }

    }
    private class AttemptLoginIn extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String token = args[2];
            String password = args[1];
            String phone = args[0];
            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("token", token));


            JSONObject json = jsonParser.makeHttpRequest(sharedPreferences.getString(URL_PORTAL_S,"")
                    +"/Login/LoginIn.php","POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {
            login.setEnabled(true);
            loginSMS.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            try {
                if (result != null) {
                    if (result.getString("success").equals("1")){
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString(SESSION_S,result.getString("session"));
                        editor.putString(USER_NAME_S,result.getString("name"));
                        editor.putString(USER_ROLE_S,result.getString("role_text"));
                        editor.putString(USER_SIGNATURE_S,result.getString("signatureFile"));
                        editor.putString(DOCUMENT_LIST_S,result.getString("documentList"));

                        editor.commit();

                        startActivity(new Intent(StartUpActivity.this,DoneRegistrationActivity.class));
                        finish();

                    }else {
                        Snackbar.make(login,result.getString("text"),Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(login,"Ошибка сети",Snackbar.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("requst",e.toString());
                Snackbar.make(login,"Ошибка сети",Snackbar.LENGTH_LONG).show();


            }



        }

    }
}
