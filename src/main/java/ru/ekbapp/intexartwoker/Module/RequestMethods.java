package ru.ekbapp.intexartwoker.Module;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.material.snackbar.Snackbar;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.ekbapp.intexartwoker.Class.REQUEST_CLASS;
import ru.ekbapp.intexartwoker.MainFragment.fragment_request;

import static ru.ekbapp.intexartwoker.StartUpActivity.SETTINGS_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.URL_PORTAL_S;

public class RequestMethods {
    Context context;
    public final String ID;
    SharedPreferences sharedPreferences;
    JSONParser jsonParser = new JSONParser();

    public RequestMethods(Context context, String ID) {
        this.context = context;
        this.ID = ID;
        sharedPreferences=context.getSharedPreferences(SETTINGS_S,Context.MODE_PRIVATE);
    }

    public String EditRequest(){
        String answer=null;
        class AttemptCreate extends AsyncTask<String, String, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected JSONObject doInBackground(String... args) {
                String address = args[3];
                String close = args[2];
                String sort = args[1];
                String session = args[0];
                String apartment=args[4];
                String direction=args[5];
                String type=args[6];
                String customer=args[7];
                String message=args[8];
                String phone=args[9];
                String note=args[10];
                String id=args[11];

                ArrayList params = new ArrayList();
                params.add(new BasicNameValuePair("session", session));
                params.add(new BasicNameValuePair("sort", sort));
                params.add(new BasicNameValuePair("close", close));
                params.add(new BasicNameValuePair("address", address));
                params.add(new BasicNameValuePair("apartment", apartment));
                params.add(new BasicNameValuePair("direction", direction));
                params.add(new BasicNameValuePair("type", type));
                params.add(new BasicNameValuePair("apartment", apartment));
                params.add(new BasicNameValuePair("customer", customer));
                params.add(new BasicNameValuePair("message", message));
                params.add(new BasicNameValuePair("phone", phone));
                params.add(new BasicNameValuePair("note", note));
                params.add(new BasicNameValuePair("id", id));





                JSONObject json = jsonParser.makeHttpRequest(sharedPreferences.getString(URL_PORTAL_S,"")
                        +"/Request/addRequest.php","POST", params);

                return json;

            }

            protected void onPostExecute(JSONObject result) {

                try {
                    if (result != null) {
                        if (result.getString("success").equals("1")){



                        }else {

                        }

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("requst",e.toString());



                }
                //  swipeRefresh.setRefreshing(false);




            }

        }
        return answer;
    }
}
