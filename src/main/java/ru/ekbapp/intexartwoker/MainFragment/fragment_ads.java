package ru.ekbapp.intexartwoker.MainFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.ekbapp.intexartwoker.Class.AdsClass;
import ru.ekbapp.intexartwoker.Module.JSONParser;
import ru.ekbapp.intexartwoker.Module.Share_void;
import ru.ekbapp.intexartwoker.R;

import static ru.ekbapp.intexartwoker.StartUpActivity.SESSION_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.SETTINGS_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.URL_PORTAL_S;

public class fragment_ads extends Fragment {
    SharedPreferences sharedPreferences;
    List<AdsClass> list=new ArrayList<>();
    ListView adsList;
    ADAPTER adapter;
    JSONParser jsonParser = new JSONParser();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_ads, container, false);
        sharedPreferences=inflater.getContext().getSharedPreferences(SETTINGS_S, Context.MODE_PRIVATE);
        adsList=root.findViewById(R.id.adsList);
        adapter=new ADAPTER(getContext());
        adsList.setAdapter(adapter);

        new AttemptGetAds().execute(sharedPreferences.getString(SESSION_S,""));



        return root;
    }
    private class AttemptGetAds extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String session = args[0];
            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("session", session));

            JSONObject json = jsonParser.makeHttpRequest(sharedPreferences.getString(URL_PORTAL_S,"")
                    +"/Ads/getAdsList.php","POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            try {
                if (result != null) {
                    if (result.getString("success").equals("1")){
                        JSONArray full_array=result.getJSONArray("Ads_array");
                        Log.e("REQUEST",full_array.toString());

                        list.clear();
                        for (int i=0;i<full_array.length();i++){
                            JSONObject obj=full_array.getJSONObject(i);
                            String date= Share_void.parseDateToddMMyyyy(obj.getString("dateCreate"));
                            list.add(new AdsClass(
                                    obj.getString("id"),
                                    "",
                                    obj.getString("AddressText"),
                                    date,
                                    obj.getString("text"),
                                    obj.getString("number"),
                                    obj.getString("creatorText")
                            ));

                        }
                        adapter.notifyDataSetChanged();
                    }else {
                        Snackbar.make(adsList,result.getString("text"),Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(adsList,"Ошибка сети", Snackbar.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("requst",e.toString());
                Snackbar.make(adsList,"Ошибка сети",Snackbar.LENGTH_LONG).show();


            }




        }

    }
    private class ADAPTER extends ArrayAdapter<AdsClass> {
        public ADAPTER(Context context) {
            super(context, R.layout.item_ads, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AdsClass ads = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_ads, null);
            }
            TextView dateCreate=convertView.findViewById(R.id.textView32);
            TextView nameCreate=convertView.findViewById(R.id.textView33);
            TextView text=convertView.findViewById(R.id.textView30);
            TextView number=convertView.findViewById(R.id.textView31);

            dateCreate.setText(ads.DateCreate);
            nameCreate.setText(ads.CreatorText);
            text.setText(ads.Text);
            number.setText("№ "+ads.Number+"\n"+ads.AddressText);

            return convertView;
        }

    }
}
