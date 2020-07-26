package ru.ekbapp.intexartwoker.MainFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import ru.ekbapp.intexartwoker.Class.REQUEST_CLASS;
import ru.ekbapp.intexartwoker.Module.JSONParser;
import ru.ekbapp.intexartwoker.Module.Share_void;
import ru.ekbapp.intexartwoker.R;
import ru.ekbapp.intexartwoker.RequestActivity;

import static ru.ekbapp.intexartwoker.StartUpActivity.SESSION_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.SETTINGS_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.URL_PORTAL_S;

public class fragment_request  extends Fragment {
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event){
        uploadList();
    }
    SharedPreferences sharedPreferences;


    BottomSheetBehavior behavior;
    String sort="all",close="active";
    RelativeLayout place;

   // SwipeRefreshLayout swipeRefresh;
    JSONParser jsonParser = new JSONParser();

    FloatingActionButton add;
    List<REQUEST_CLASS> list=new ArrayList<>();
    List<REQUEST_CLASS> listFocuse=new ArrayList<>();
    List<String> address=new ArrayList<>();
    ADAPTER adapter;
    ADAPTER iAdapter;
    EAdapter eAdapter;
    ArrayAdapter<String> arrayAddressAdapter;
    Button all,user,addressBTN,status;
    AutoCompleteTextView addressFilter;
    RadioButton r1,r2,r3;
    final static String CLOSE="CLOSE";

    ListView listView,focuse;
    ExpandableListView expandableListView;
    ProgressBar progressBar;
    CheckBox electrician,plumbing,improvement,building;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_request, container, false);

        TabHost tabHost = (TabHost) root.findViewById(R.id.tabHost);


        all=root.findViewById(R.id.button14);
        user=root.findViewById(R.id.button16);
        addressBTN=root.findViewById(R.id.button17);
        status=root.findViewById(R.id.button18);
        add=root.findViewById(R.id.floatingActionButton);
        addressFilter=root.findViewById(R.id.autoEnd);

        r1=root.findViewById(R.id.radioButton);
        r2=root.findViewById(R.id.radioButton2);
        r3=root.findViewById(R.id.radioButton3);
        place=root.findViewById(R.id.place);
        progressBar=root.findViewById(R.id.progressBar);
        focuse=root.findViewById(R.id.focuse);

        sharedPreferences=inflater.getContext().getSharedPreferences(SETTINGS_S, Context.MODE_PRIVATE);

        electrician=root.findViewById(R.id.checkBox2);
        plumbing=root.findViewById(R.id.checkBox);
        building=root.findViewById(R.id.checkBox3);
        improvement=root.findViewById(R.id.checkBox4);

        electrician.setChecked(sharedPreferences.getBoolean("electrician",true));
        plumbing.setChecked(sharedPreferences.getBoolean("plumbing",true));
        building.setChecked(sharedPreferences.getBoolean("building",false));
        improvement.setChecked(sharedPreferences.getBoolean("improvement",false));

        electrician.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("electrician",isChecked);
                editor.commit();
            }
        });
        plumbing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("plumbing",isChecked);
                editor.commit();
            }
        });
        building.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("building",isChecked);
                editor.commit();
            }
        });
        improvement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("improvement",isChecked);
                editor.commit();
            }
        });




        close=sharedPreferences.getString(CLOSE,"active");
        switch (sharedPreferences.getString(CLOSE,"active")){
            case "all":
                r3.setChecked(true);
                break;
            case "close":
                r2.setChecked(true);
                break;
            case "active":
                r1.setChecked(true);
                break;
        }

        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    close="active";
                    saveClose(close);
                }
            }
        });
        r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    close="close";
                    saveClose(close);
                }
            }
        });
        r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    close="all";
                    saveClose(close);
                }
            }
        });

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");

        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Список");
        tabHost.addTab(tabSpec);

        //TODO добавить фокусировку

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Важное");
        tabHost.addTab(tabSpec);


        /*
        swipeRefresh=root.findViewById(R.id.swipeRefresh);

        swipeRefresh.setRefreshing(true);


        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AttemptGetRequest().execute(sharedPreferences.getString(SESSION_S,""),sort,close);
            }
        });
*/

        View bottomSheet =root.findViewById(R.id.bottomSheet);
        behavior=BottomSheetBehavior.from(bottomSheet);
        TextView filter_text=root.findViewById(R.id.filter_text);
        filter_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (behavior.getState()==BottomSheetBehavior.STATE_COLLAPSED){
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }else{
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                sort="user";
               // swipeRefresh.setRefreshing(false);
                uploadList();

            }
        });
        addressBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                sort="address";
                //swipeRefresh.setRefreshing(false);
                uploadList();

            }
        });
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                sort="status";
               // swipeRefresh.setRefreshing(false);
                uploadList();

            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                //swipeRefresh.setRefreshing(true);
                sort="all";
                uploadList();

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                ViewGroup viewGroup = root.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_add_request, viewGroup, false);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                Button create =dialogView.findViewById(R.id.button20);
                final AutoCompleteTextView addressAC=dialogView.findViewById(R.id.autoCompleteTextView2);
                if (arrayAddressAdapter!=null){
                    addressAC.setAdapter(arrayAddressAdapter);
                }
                final EditText apartment=dialogView.findViewById(R.id.editText7);
                final EditText customer=dialogView.findViewById(R.id.editText8);
                final MaskedEditText phone=dialogView.findViewById(R.id.phone);
                final EditText message=dialogView.findViewById(R.id.editText9);
                final EditText note=dialogView.findViewById(R.id.editText10);
                final Spinner direction=dialogView.findViewById(R.id.spinner2);
                final Spinner type=dialogView.findViewById(R.id.spinner3);

                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         if (direction.getSelectedItemPosition()==0){
                             Snackbar.make(apartment,"Укажите направление",Snackbar.LENGTH_LONG).show();
                             return;
                         }
                        if (type.getSelectedItemPosition()==0){
                            Snackbar.make(apartment,"Укажите тип",Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        if (apartment.getText().toString().equals("")){
                            Snackbar.make(apartment,"Укажите квартиру",Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        if (addressAC.getText().toString().equals("")){
                            Snackbar.make(apartment,"Укажите адрес",Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        if (!address.contains(addressAC.getText().toString())){
                            Snackbar.make(apartment,"Укажите адрес из списка",Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        if (customer.getText().toString().equals("")){
                            Snackbar.make(apartment,"Укажите заказчика",Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        if (message.getText().toString().equals("")){
                            Snackbar.make(apartment,"Укажите сообщение",Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        if (phone.getRawText().length()!=10){
                            Snackbar.make(apartment,"Укажите телефон",Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        //swipeRefresh.setRefreshing(true);
                        new AttemptCreate().execute(
                                sharedPreferences.getString(SESSION_S,""),
                                sort,
                                close,
                                addressAC.getText().toString(),
                                apartment.getText().toString(),
                                direction.getSelectedItem().toString(),
                                type.getSelectedItem().toString(),
                                customer.getText().toString(),
                                message.getText().toString(),
                                phone.getRawText(),
                                note.getText().toString()
                        );
                       // swipeRefresh.setRefreshing(true);
                        progressBar.setVisibility(View.VISIBLE);
                        alertDialog.dismiss();
                    }
                });

                dialogView.findViewById(R.id.button19).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            }
        });

        adapter=new ADAPTER(getContext(),list);
        eAdapter=new EAdapter(getContext());
        iAdapter=new ADAPTER(getContext(),listFocuse);

        uploadList();
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(0,0,0,45);
        listView =new ListView(getContext());
        listView.setLayoutParams(lparams);


        expandableListView =new ExpandableListView(getContext());
        expandableListView.setLayoutParams(lparams);


        return root;
    }
    void saveClose(String c){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(CLOSE,c);
        editor.commit();
    }
    private class AttemptGetRequest extends AsyncTask<String, String, JSONObject> {
        public AttemptGetRequest() {
            list.clear();
            adapter.notifyDataSetChanged();
            eAdapter=new EAdapter(getContext());
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String improvement = args[7];
            String building = args[6];
            String electrician = args[5];
            String plumbing = args[4];
            String address = args[3];
            String close = args[2];
            String sort = args[1];
            String session = args[0];
            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("session", session));
            params.add(new BasicNameValuePair("sort", sort));
            params.add(new BasicNameValuePair("close", close));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("plumbing", plumbing));
            params.add(new BasicNameValuePair("electrician", electrician));
            params.add(new BasicNameValuePair("building", building));
            params.add(new BasicNameValuePair("improvement", improvement));



            JSONObject json = jsonParser.makeHttpRequest(sharedPreferences.getString(URL_PORTAL_S,"")
                    +"/Request/getRequestList.php","POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            try {
                if (result != null) {
                    if (result.getString("success").equals("1")){
                        JSONArray full_array=result.getJSONArray("full_array");
                        JSONArray address_array=result.getJSONArray("address");
                        JSONArray focuse_array=result.getJSONArray("focus");
                        Log.e("REQUEST",full_array.toString());
                        if (result.getString("create").equals("1")){
                            add.setVisibility(View.VISIBLE);
                        }else {
                            add.setVisibility(View.GONE);
                        }
                        list.clear();
                        listFocuse.clear();
                        for (int i=0;i<full_array.length();i++){
                            JSONObject obj=full_array.getJSONObject(i);
                            String date= Share_void.parseDateToddMMyyyy(obj.getString("dateCreate"));
                            list.add(new REQUEST_CLASS(
                                    obj.getString("id"),
                                    obj.getString("number"),
                                    obj.getString("addressText"),
                                    obj.getString("apartmentText"),
                                    date,
                                    obj.getString("customerPhone"),
                                    obj.getString("message"),
                                    obj.getString("status"),
                                    obj.getString("direction"),
                                    obj.getString("type"),
                                    obj.getString("full_name")
                            ));
                        }
                        for (int i=0;i<focuse_array.length();i++){
                            JSONObject obj=focuse_array.getJSONObject(i);
                            String date= Share_void.parseDateToddMMyyyy(obj.getString("dateCreate"));
                            listFocuse.add(new REQUEST_CLASS(
                                    obj.getString("id"),
                                    obj.getString("number"),
                                    obj.getString("addressText"),
                                    obj.getString("apartmentText"),
                                    date,
                                    obj.getString("customerPhone"),
                                    obj.getString("message"),
                                    obj.getString("status"),
                                    obj.getString("direction"),
                                    obj.getString("type"),
                                    obj.getString("full_name")
                            ));
                        }
                        address.clear();
                        for (int i=0;i<address_array.length();i++){
                            JSONObject obj=address_array.getJSONObject(i);
                            address.add(obj.getString("address"));
                        }
                        arrayAddressAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_dropdown_item_1line, address);
                        addressFilter.setAdapter(arrayAddressAdapter);


                        adapter.notifyDataSetChanged();
                        iAdapter.notifyDataSetChanged();
                        eAdapter=new EAdapter(getContext());

                        place.removeAllViews();
                        if (sort.equals("all")){

                            place.addView(listView);
                            listView.setAdapter(adapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    startActivity(new Intent(getContext(), RequestActivity.class).putExtra("id",list.get(position).getId()).putExtra("number",list.get(position).getNumber()));
                                }
                            });
                        }else {

                            expandableListView.setAdapter(eAdapter);


                            place.addView(expandableListView);
                        }
                        Log.e("DIRECTION",result.getString("direction"));
                    }else {
                        Snackbar.make(add,result.getString("text"),Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(add,"Ошибка сети",Snackbar.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("requst",e.toString());
                Snackbar.make(add,"Ошибка сети",Snackbar.LENGTH_LONG).show();


            }
          //  swipeRefresh.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
        }

    }
    private class ADAPTER extends ArrayAdapter<REQUEST_CLASS> {
        List<REQUEST_CLASS> list;
        public ADAPTER(Context context,List<REQUEST_CLASS> l) {
            super(context, R.layout.item_request, l);
            list=l;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            REQUEST_CLASS request = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_request, null);
            }
            ((TextView) convertView.findViewById(R.id.textView3)).setText("№"+request.number);
            ((TextView) convertView.findViewById(R.id.textView5)).setText(request.status);
            ((TextView) convertView.findViewById(R.id.textView10)).setText(request.message);
            ((TextView) convertView.findViewById(R.id.textView11)).setText(request.dateCreate);
            ((TextView) convertView.findViewById(R.id.textView12)).setText(request.AddressText+" кв. "+request.apartmentText);
            ((TextView) convertView.findViewById(R.id.textView13)).setText(request.phoneCustomer);
            ((TextView) convertView.findViewById(R.id.textView14)).setText(request.responsible);
            ((TextView) convertView.findViewById(R.id.textView8)).setText(request.direction);
            ((TextView) convertView.findViewById(R.id.textView9)).setText(request.type);

            switch (request.status){
                case "Новая":
                    ((TextView) convertView.findViewById(R.id.textView5)).setTextColor(getResources().getColor(R.color.new_r));
                    break;
                case "Назначен исполнитель":
                    ((TextView) convertView.findViewById(R.id.textView5)).setTextColor(getResources().getColor(R.color.list_spiner));
                    break;
                case "Закрыто":
                    ((TextView) convertView.findViewById(R.id.textView5)).setTextColor(getResources().getColor(R.color.error));
                    break;
                case "В работе":
                    ((TextView) convertView.findViewById(R.id.textView5)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    break;

            }
            switch (request.direction){
                case "Сантехника":
                    ((TextView) convertView.findViewById(R.id.textView8)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    break;
                case "Электрика":
                    ((TextView) convertView.findViewById(R.id.textView8)).setTextColor(getResources().getColor(R.color.electrika));
                    break;
                default:
                    break;
            }

            return convertView;
        }

    }
    class EAdapter extends BaseExpandableListAdapter{
        final Context context;

        HashMap<String, List<REQUEST_CLASS>> listMap=new HashMap<>();
        List<String> head=new ArrayList<>();



        public EAdapter(Context context) {
            this.context = context;
            Log.e("ADAPTER","запуск адаптера "+list.size());

            for (REQUEST_CLASS request_class : list){
                Log.e("ADAPTER","Сортеруем "+request_class.getId());
                String headString="";
                switch (sort){
                    case "user":
                        headString=request_class.getResponsible();
                        break;
                    case "address":
                        headString=request_class.getAddressText();
                        break;
                    case "status":
                        headString=request_class.getStatus();
                        break;
                }
                if (!head.contains(headString)){
                    head.add(headString);
                    List<REQUEST_CLASS> newList=new ArrayList<>();
                    newList.add(request_class);
                    listMap.put(headString,newList);

                }else {
                    List<REQUEST_CLASS> m = listMap.get(headString);
                    if (m!=null){
                        m.add(request_class);
                        if (m!=null){
                            listMap.put(headString,m);
                        }

                    }else{
                        List<REQUEST_CLASS> newList=new ArrayList<>();
                        newList.add(request_class);
                        if (newList!=null){
                            listMap.put(headString,newList);
                        }

                    }
                }
            }
            notifyDataSetChanged();
            Log.e("ADAPTER","end "+head.size());

        }

        @Override
        public int getGroupCount() {
            return head.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            List childList = listMap.get(head.get(groupPosition));
            if (childList != null && ! childList.isEmpty()) {
                return childList.size();
            }
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return  head.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return listMap.get(head.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.master_iten_groop, null);
            }
            TextView head = (TextView) convertView.findViewById(R.id.textView24);
            head.setText(headerTitle);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final REQUEST_CLASS request = (REQUEST_CLASS) getChild(groupPosition,childPosition);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_request, null);
            }
            ((TextView) convertView.findViewById(R.id.textView3)).setText("№"+request.number);
            ((TextView) convertView.findViewById(R.id.textView5)).setText(request.status);
            ((TextView) convertView.findViewById(R.id.textView10)).setText(request.message);
            ((TextView) convertView.findViewById(R.id.textView11)).setText(request.dateCreate);
            ((TextView) convertView.findViewById(R.id.textView12)).setText(request.AddressText+" кв. "+request.apartmentText);
            ((TextView) convertView.findViewById(R.id.textView13)).setText(request.phoneCustomer);
            ((TextView) convertView.findViewById(R.id.textView14)).setText(request.responsible);
            ((TextView) convertView.findViewById(R.id.textView8)).setText(request.direction);
            ((TextView) convertView.findViewById(R.id.textView9)).setText(request.type);

            switch (request.status){
                case "Новая":
                    ((TextView) convertView.findViewById(R.id.textView5)).setTextColor(getResources().getColor(R.color.new_r));
                    break;
                case "Назначен исполнитель":
                    ((TextView) convertView.findViewById(R.id.textView5)).setTextColor(getResources().getColor(R.color.list_spiner));
                    break;
                case "Закрыто":
                    ((TextView) convertView.findViewById(R.id.textView5)).setTextColor(getResources().getColor(R.color.error));
                    break;

            }
            switch (request.direction){
                case "Сантехника":
                    ((TextView) convertView.findViewById(R.id.textView8)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    break;
                case "Электрика":
                    ((TextView) convertView.findViewById(R.id.textView8)).setTextColor(getResources().getColor(R.color.electrika));
                    break;
            }
            if (request!=null){
                if (getChild(groupPosition,childPosition)!=null){
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(
                                    new Intent(getContext(),RequestActivity.class)
                                            .putExtra("id",request.getId())
                                            .putExtra("number",request.getNumber())
                            );
                        }
                    });
                }
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
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





            JSONObject json = jsonParser.makeHttpRequest(sharedPreferences.getString(URL_PORTAL_S,"")
                    +"/Request/addRequest.php","POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            try {
                if (result != null) {
                    if (result.getString("success").equals("1")){
                        JSONArray full_array=result.getJSONArray("full_array");
                        JSONArray address_array=result.getJSONArray("address");
                        Log.e("REQUEST",full_array.toString());
                        if (result.getString("create").equals("1")){
                            add.setVisibility(View.VISIBLE);
                        }else {
                            add.setVisibility(View.GONE);
                        }
                        list.clear();
                        for (int i=0;i<full_array.length();i++){
                            JSONObject obj=full_array.getJSONObject(i);
                            String date= Share_void.parseDateToddMMyyyy(obj.getString("dateCreate"));
                            list.add(new REQUEST_CLASS(
                                    obj.getString("id"),
                                    obj.getString("number"),
                                    obj.getString("addressText"),
                                    obj.getString("apartmentText"),
                                    date,
                                    obj.getString("customerPhone"),
                                    obj.getString("message"),
                                    obj.getString("status"),
                                    obj.getString("direction"),
                                    obj.getString("type"),
                                    obj.getString("full_name")
                            ));
                        }
                        address.clear();
                        for (int i=0;i<address_array.length();i++){
                            JSONObject obj=address_array.getJSONObject(i);
                            address.add(obj.getString("address"));
                        }
                        arrayAddressAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_dropdown_item_1line, address);
                        addressFilter.setAdapter(arrayAddressAdapter);


                        adapter.notifyDataSetChanged();
                        eAdapter=new EAdapter(getContext());


                    }else {
                        Snackbar.make(add,result.getString("text"),Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(add,"Ошибка сети",Snackbar.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("requst",e.toString());
                Snackbar.make(add,"Ошибка сети",Snackbar.LENGTH_LONG).show();


            }
          //  swipeRefresh.setRefreshing(false);
            progressBar.setVisibility(View.GONE);



        }

    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        uploadList();

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
    private void uploadList(){
        progressBar.setVisibility(View.VISIBLE);
        new AttemptGetRequest().execute(
                sharedPreferences.getString(SESSION_S,""),sort,close,addressFilter.getText().toString(),
                (plumbing.isChecked())?"1":"",
                (electrician.isChecked())?"1":"",
                (building.isChecked())?"1":"",
                (improvement.isChecked())?"1":"");
    }


}
