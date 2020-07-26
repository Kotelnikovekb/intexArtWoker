package ru.ekbapp.intexartwoker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import ru.ekbapp.intexartwoker.Class.JsonHelper;
import ru.ekbapp.intexartwoker.MainFragment.fragment_request;
import ru.ekbapp.intexartwoker.Module.JSONParser;
import ru.ekbapp.intexartwoker.Module.Share_void;

import static ru.ekbapp.intexartwoker.Module.Share_void.SQL_FORMAT;
import static ru.ekbapp.intexartwoker.StartUpActivity.SESSION_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.SETTINGS_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.URL_PORTAL_S;

public class RequestActivity extends AppCompatActivity {

    String number,id;
    JSONParser jsonParser = new JSONParser();
    SharedPreferences sharedPreferences;
    LinearLayout loadingLiner,infoLiner;
    List<HistoryClass> historyList =new ArrayList<>();
    List<WorkerClass> workersList=new ArrayList<>();
    TextView message,customer,executorRead,direction,type,status,dateCreate,dateClose,note,creator,masterName,timeInWorkTitle;
    Button map,call,executor,addNote;
    ContentAdapter mAdapter;
    RecyclerView historyView;
    EditText noteET;
    ProgressBar progressBar;
    Spinner wokers;
    WorkerAdapter workerAdapter;
    int selectWorker=-1;
    Button createDocument;
    public static final String PRINT_ID="PRINT_ID";
    public static final String PRINT_NUMBER="NUMBER_ID";
    Button startBTN,editBTN;
    Chronometer timeInWork;
    ArrayAdapter<String> arrayAddressAdapter;
    List<String> address=new ArrayList<>();
    String STREET="",APATMENT="",CUSTOMER_NAME="",CUSTOMER_PHONE="",MESSAGE="",NOTE="",DIRECTION="",TYPE="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        number=getIntent().getStringExtra("number");
        id=getIntent().getStringExtra("id");
        setTitle("Заявка № "+number);
        setContentView(R.layout.activity_request);
        sharedPreferences=getSharedPreferences(SETTINGS_S,MODE_PRIVATE);


        message=findViewById(R.id.textView19);
        map=findViewById(R.id.button);
        call=findViewById(R.id.button2);
        customer=findViewById(R.id.textView20);
        executor=findViewById(R.id.button4);
        executorRead=findViewById(R.id.textView23);
        status=findViewById(R.id.textView16);
        direction=findViewById(R.id.textView17);
        type=findViewById(R.id.textView18);
        dateCreate=findViewById(R.id.textView24);
        dateClose=findViewById(R.id.textView25);
        note=findViewById(R.id.textView26);
        creator=findViewById(R.id.textView27);
        masterName=findViewById(R.id.textView28);
        noteET=findViewById(R.id.editText2);
        startBTN=findViewById(R.id.button21);
        timeInWork=findViewById(R.id.timeInWork);
        timeInWorkTitle=findViewById(R.id.timeInWorkTitle);
        editBTN=findViewById(R.id.button22);

        createDocument=findViewById(R.id.button8);

        workerAdapter=new WorkerAdapter(RequestActivity.this,R.layout.item_woker,workersList);


        historyView=findViewById(R.id.recycler_view);
        mAdapter=new ContentAdapter(historyList);
        addNote=findViewById(R.id.button7);

        wokers=findViewById(R.id.spinner);
        wokers.setAdapter(workerAdapter);

        progressBar=findViewById(R.id.progressBar3);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        historyView.setLayoutManager(mLayoutManager);
        historyView.setItemAnimator(new DefaultItemAnimator());

        historyView.setAdapter(mAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadingLiner=findViewById(R.id.loadingLiner);
        infoLiner=findViewById(R.id.infoLiner);
        TabHost tabHost = (TabHost) findViewById(R.id.tabsR);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");

        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("О заявке");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("История");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("Связи");
        tabHost.addTab(tabSpec);

        new AttemptDownloadInfo().execute(sharedPreferences.getString(SESSION_S,""),id);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noteET.getText().toString().equals("")){
                    progressBar.setVisibility(View.VISIBLE);
                    new AttemptAddNote().execute(sharedPreferences.getString(SESSION_S,""),id,noteET.getText().toString());

                }
            }
        });
        final String idRequest=id;
        wokers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if (selectWorker!=-1&&selectWorker!=position){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RequestActivity.this);
                    builder.setMessage("Сделать "+workersList.get(position).getName()+" исполнителем?");
                    builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            progressBar.setVisibility(View.VISIBLE);
                            new AttemptSetExecutor().execute(sharedPreferences.getString(SESSION_S,""),
                                    idRequest,workersList.get(position).getId());
                            dialog.dismiss();

                        }
                    });
                    builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            wokers.setSelection(selectWorker);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        startBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new AttemptStartWork().execute(sharedPreferences.getString(SESSION_S,""),id);
            }
        });

        editBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RequestActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_add_request, viewGroup, false);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                Button create =dialogView.findViewById(R.id.button20);
                final AutoCompleteTextView addressAC=dialogView.findViewById(R.id.autoCompleteTextView2);
                if (arrayAddressAdapter!=null){
                    addressAC.setAdapter(arrayAddressAdapter);
                }
                create.setText("Изменить");
                final EditText apartment=dialogView.findViewById(R.id.editText7);
                final EditText customer=dialogView.findViewById(R.id.editText8);
                final MaskedEditText phone=dialogView.findViewById(R.id.phone);
                final EditText message=dialogView.findViewById(R.id.editText9);
                final EditText note=dialogView.findViewById(R.id.editText10);
                final Spinner direction=dialogView.findViewById(R.id.spinner2);
                final Spinner type=dialogView.findViewById(R.id.spinner3);

                TextView titleDialog=dialogView.findViewById(R.id.titleDialog);
                titleDialog.setText("Изменение заявки");

                addressAC.setText(STREET);
                apartment.setText(APATMENT);
                phone.setText(CUSTOMER_PHONE);
                customer.setText(CUSTOMER_NAME);
                note.setText(NOTE);
                message.setText(MESSAGE);

                switch (DIRECTION){
                    case "Сантехника":
                        direction.setSelection(1);
                        break;
                    case "Электрика":
                        direction.setSelection(2);
                        break;
                    case "Общестрой":
                        direction.setSelection(3);
                        break;
                    case "Благоустройство":
                        direction.setSelection(4);
                        break;
                }
                switch (TYPE){
                    case "Аварийная":
                        type.setSelection(1);
                        break;
                    case "Платная":
                        type.setSelection(2);
                        break;
                    case "На время":
                        type.setSelection(3);
                        break;
                    case "Текущая":
                        type.setSelection(4);
                        break;


                }



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

                        new AttemptEditRequest().execute(
                                sharedPreferences.getString(SESSION_S,""),
                                addressAC.getText().toString(),
                                apartment.getText().toString(),
                                direction.getSelectedItem().toString(),
                                type.getSelectedItem().toString(),
                                customer.getText().toString(),
                                message.getText().toString(),
                                phone.getRawText(),
                                note.getText().toString(),
                                idRequest
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

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre,date;

        private ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
            date = (TextView) view.findViewById(R.id.date);
        }
    }
    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<HistoryClass> list=new ArrayList<>();

        public ContentAdapter(List<HistoryClass> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_history, viewGroup, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            if(list.get(i).getDateCreate().isEmpty()){
                viewHolder.year.setVisibility(View.GONE) ;
            }else{
                viewHolder.year.setText(list.get(i).getDateCreate());
            }
            viewHolder.genre.setText(list.get(i).getTitle());
            viewHolder.title.setText(list.get(i).getText());
            viewHolder.date.setText(list.get(i).getTimeCreate());


        }

        // Return size of dataset
        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    class AttemptDownloadInfo extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String session = args[0];
            String id = args[1];
            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("session", session));
            params.add(new BasicNameValuePair("id", id));



            JSONObject json = jsonParser.makeHttpRequest(sharedPreferences.getString(URL_PORTAL_S,"")
                    +"/Request/getRequestInfo.php","POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            try {
                if (result != null) {
                    if (result.getString("success").equals("1")){
                        Log.e("REQUEST",result.toString());
                        loadingLiner.setVisibility(View.GONE);
                        infoLiner.setVisibility(View.VISIBLE);

                        final String phone=result.getString("customerPhone");

                        message.setText(result.getString("message"));
                        map.setText(result.getString("addressText"));
                        call.setText(phone);
                        customer.setText("Заказчик: "+result.getString("customerText"));
                        executor.setText(result.getString("executor"));
                        executorRead.setText(result.getString("executor_read"));
                        status.setText(result.getString("status"));
                        direction.setText(result.getString("direction"));
                        type.setText(result.getString("type"));
                        creator.setText(result.getString("creator"));
                        masterName.setText(result.getString("master"));
                        note.setText("Примечание: "+result.getString("note"));

                        STREET=result.getString("street");
                        APATMENT=result.getString("apartment");
                        CUSTOMER_NAME=result.getString("customerText");
                        CUSTOMER_PHONE=result.getString("customerPhone");
                        MESSAGE=result.getString("message");
                        NOTE=result.getString("note");
                        TYPE=result.getString("type");
                        DIRECTION=result.getString("direction");

                        if (result.getString("note").equals(""))
                            note.setVisibility(View.GONE);

                        dateCreate.setText("Создано:\n"+Share_void.parseDateToddMMyyyyHHmm(result.getString("dateCreate")));
                        dateClose.setText("Закрыто:\n"+Share_void.parseDateToddMMyyyyHHmm(result.getString("dateEnd")));
                        if (result.getString("dateEnd").equals("0000-00-00 00:00:00")){
                            dateClose.setVisibility(View.GONE);
                        }
                        if (!result.getString("StartWork").equals("0000-00-00 00:00:00")){
                            //timeInWork.setVisibility(View.VISIBLE);
                            //timeInWorkTitle.setVisibility(View.VISIBLE);
                            try {
                                Date dateStart=SQL_FORMAT.parse(result.getString("StartWork"));
                                Date dateNow=SQL_FORMAT.parse(result.getString("today"));
                                long base= (dateNow.getTime()-dateStart.getTime());
                                Log.e("time","||"+result.getString("StartWork")+"|| "+base+"");
                                timeInWork.setBase(base);
                                timeInWork.start();
                            }catch (ParseException e){

                            }

                        }

                        final String v1=result.getString("v1");
                        final String v2=result.getString("v2");


                        if (!result.getString("setExecutor").equals("1")){
                            wokers.setEnabled(false);
                        }
                        if (!result.getString("Edit").equals("1")){
                            editBTN.setVisibility(View.GONE);
                        }
                        if (!result.getString("RIGHTS_CLOSE").equals("1")){
                            createDocument.setVisibility(View.GONE);
                            createDocument.setEnabled(false);
                        }else {
                            createDocument.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int permissionWrite = ContextCompat.checkSelfPermission(RequestActivity.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                    int permissionRead = ContextCompat.checkSelfPermission(RequestActivity.this,
                                            Manifest.permission.READ_EXTERNAL_STORAGE);
                                    if (permissionWrite== PackageManager.PERMISSION_GRANTED&&permissionRead==PackageManager.PERMISSION_GRANTED){
                                        SharedPreferences.Editor editor=sharedPreferences.edit();
                                        editor.putString(PRINT_ID,id);
                                        editor.putString(PRINT_NUMBER,number);
                                        editor.commit();
                                        startActivity(new Intent(RequestActivity.this,DocumerCreatorActivity.class));
                                    }else {
                                        Snackbar.make(progressBar,"Нет доступа к хранилищу",Snackbar.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                        if (!result.getString("RIGHTS_START").equals("1")){
                            startBTN.setVisibility(View.GONE);
                            startBTN.setEnabled(false);
                        }

                        JSONArray historyArray=result.getJSONArray("historyArray");
                        historyList.clear();
                        for (int i=0;i<historyArray.length();i++){
                            JSONObject obj=historyArray.getJSONObject(i);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date _date = null;
                            try {
                                _date = dateFormat.parse(obj.getString("dateCreate"));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            String month =
                                    DateFormat.getDateInstance(SimpleDateFormat.LONG, new Locale("ru")).format(_date);
                            String dateCreate=month;
                            for (HistoryClass yeas : historyList){
                                if (dateCreate.equals(yeas.getDateCreate())){
                                    dateCreate="";
                                    break;
                                }

                            }

                            historyList.add(new HistoryClass(obj.getString("heading"),
                                    obj.getString("text"),dateCreate,Share_void.parseDateToHHmm(obj.getString("dateCreate")),
                                    obj.getString("creatorText")));
                        }

                        mAdapter.notifyDataSetChanged();

                        if (result.getString("setExecutor").equals("1")){
                            executor.setVisibility(View.GONE);
                            workersList.clear();

                            if (result.getString("executor").equals("Не назначен")){
                                workersList.add(new WorkerClass(
                                        "0",
                                        "Не указан",
                                        "Выберете исполнителя"));
                                wokers.setSelection(workersList.size());
                            }
                            JSONArray wokerArray=result.getJSONArray("wokerArray");
                            for (int i=0;i< wokerArray.length();i++){
                                JSONObject obj=wokerArray.getJSONObject(i);

                                workersList.add(new WorkerClass(
                                        obj.getString("id"),
                                        obj.getString("full_name"),
                                        obj.getString("role_text")+" заявок:  "+obj.getString("workCount")));
                            }
                            workerAdapter.notifyDataSetChanged();

                            JSONArray address_array=result.getJSONArray("addressArray");
                            address.clear();
                            for (int i=0;i<address_array.length();i++){
                                JSONObject obj=address_array.getJSONObject(i);
                                address.add(obj.getString("address"));
                            }
                            arrayAddressAdapter=new ArrayAdapter<>(RequestActivity.this,android.R.layout.simple_dropdown_item_1line, address);
                            if (result.getString("executor").equals("Не назначен")){
                                wokers.setSelection(0);
                                selectWorker=0;
                            }else {
                                for (WorkerClass worker : workersList){
                                    if (worker.getName().equals(result.getString("executor"))){
                                        wokers.setSelection(workersList.indexOf(worker));
                                        selectWorker=workersList.indexOf(worker);
                                    }
                                }
                            }

                        }else{
                            wokers.setVisibility(View.GONE);
                        }
                        map.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent =
                                        new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?"  +
                                                        "&daddr=" + v1 + ","
                                                        + v2));
                                startActivity(intent);
                            }
                        });
                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                                startActivity(intent);
                            }
                        });
                        File folderRequest= new File(Environment.getExternalStorageDirectory() +
                                File.separator+getResources().getString(R.string.folder_name)+
                                File.separator+getResources().getString(R.string.folder_request)+
                                File.separator+number
                        );
                        if (!folderRequest.exists()){
                            folderRequest.mkdirs();
                            Log.e("PERMISSION","Папки не существует!");
                            Log.e("PERMISSION","Создаю папку "+folderRequest.mkdirs());
                        }


                        final File JSONREQUEST = new File(folderRequest, sharedPreferences.getString(PRINT_NUMBER,"")+".txt");
                        JsonHelper jsonHelper=new JsonHelper(JSONREQUEST);
                        jsonHelper.createJson(result);


                        if (result.getString("status").equals("Закрыто")){
                            wokers.setEnabled(false);
                            createDocument.setVisibility(View.GONE);
                            createDocument.setEnabled(false);

                        }
                    }else {
                        Snackbar.make(loadingLiner,result.getString("text"),Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(loadingLiner,"Ошибка сети",Snackbar.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("requst",e.toString());
                Snackbar.make(loadingLiner,"Ошибка сети",Snackbar.LENGTH_LONG).show();


            }
        }

    }
    class AttemptStartWork extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String session = args[0];
            String id = args[1];
            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("session", session));
            params.add(new BasicNameValuePair("id", id));



            JSONObject json = jsonParser.makeHttpRequest(sharedPreferences.getString(URL_PORTAL_S,"")
                    +"/Request/startWork.php","POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            try {
                if (result != null) {
                    if (result.getString("success").equals("1")){
                        timeInWork.setVisibility(View.VISIBLE);
                        timeInWork.start();
                        timeInWorkTitle.setVisibility(View.VISIBLE);
                        startBTN.setVisibility(View.GONE);
                        status.setText("В работе");;
                    }else {
                        Snackbar.make(loadingLiner,result.getString("text"),Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(loadingLiner,"Ошибка сети",Snackbar.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("requst",e.toString());
                Snackbar.make(loadingLiner,"Ошибка сети",Snackbar.LENGTH_LONG).show();


            }
            progressBar.setVisibility(View.GONE);
        }

    }
    class AttemptAddNote extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String session = args[0];
            String id = args[1];
            String note = args[2];
            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("session", session));
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("note", note));



            JSONObject json = jsonParser.makeHttpRequest(sharedPreferences.getString(URL_PORTAL_S,"")
                    +"/Request/addComment.php","POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            try {
                if (result != null) {
                    if (result.getString("success").equals("1")){
                        noteET.setText("");
                        JSONArray historyArray=result.getJSONArray("historyArray");
                        historyList.clear();
                        for (int i=0;i<historyArray.length();i++){
                            JSONObject obj=historyArray.getJSONObject(i);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date _date = null;
                            try {
                                _date = dateFormat.parse(obj.getString("dateCreate"));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            String month =
                                    DateFormat.getDateInstance(SimpleDateFormat.LONG, new Locale("ru")).format(_date);
                            String dateCreate=month;
                            for (HistoryClass yeas : historyList){
                                if (dateCreate.equals(yeas.getDateCreate())){
                                    dateCreate="";
                                    break;
                                }

                            }

                            historyList.add(new HistoryClass(obj.getString("heading"),
                                    obj.getString("text"),dateCreate,Share_void.parseDateToHHmm(obj.getString("dateCreate")),
                                    obj.getString("creatorText")));
                        }

                        mAdapter.notifyDataSetChanged();

                    }else {
                        Snackbar.make(progressBar,result.getString("text"),Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(progressBar,"Ошибка сети",Snackbar.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("requst",e.toString());
                Snackbar.make(progressBar,"Ошибка сети",Snackbar.LENGTH_LONG).show();


            }
            progressBar.setVisibility(View.GONE);



        }

    }
    class AttemptSetExecutor extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String session = args[0];
            String id = args[1];
            String idWorker = args[2];
            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("session", session));
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("idWorker", idWorker));



            JSONObject json = jsonParser.makeHttpRequest(sharedPreferences.getString(URL_PORTAL_S,"")
                    +"/Request/setExecutor.php","POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            try {
                if (result != null) {
                    Log.e("REQUEST",result.toString());
                    if (result.getString("success").equals("1")){
                        Snackbar.make(progressBar,result.getString("newExecutor")+" назначен",Snackbar.LENGTH_LONG).show();
                        JSONArray historyArray=result.getJSONArray("historyArray");
                        historyList.clear();
                        for (int i=0;i<historyArray.length();i++){
                            JSONObject obj=historyArray.getJSONObject(i);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date _date = null;
                            try {
                                _date = dateFormat.parse(obj.getString("dateCreate"));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            String month =
                                    DateFormat.getDateInstance(SimpleDateFormat.LONG, new Locale("ru")).format(_date);
                            String dateCreate=month;
                            for (HistoryClass yeas : historyList){
                                if (dateCreate.equals(yeas.getDateCreate())){
                                    dateCreate="";
                                    break;
                                }

                            }

                            historyList.add(new HistoryClass(obj.getString("heading"),
                                    obj.getString("text"),dateCreate,Share_void.parseDateToHHmm(obj.getString("dateCreate")),
                                    obj.getString("creatorText")));
                        }
                        status.setText(result.getString("newStatus3"));

                        mAdapter.notifyDataSetChanged();
                    }else {
                        Snackbar.make(progressBar,result.getString("text"),Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(progressBar,"Ошибка сети",Snackbar.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("requst",e.toString());
                Snackbar.make(progressBar,"Ошибка сети",Snackbar.LENGTH_LONG).show();


            }
            progressBar.setVisibility(View.GONE);



        }

    }
    class HistoryClass{
        public String title;
        public String text;
        public String dateCreate;
        public String timeCreate;
        public String creator;

        public HistoryClass(String title, String text, String dateCreate, String timeCreate, String creator) {
            this.title = title;
            this.text = text;
            this.dateCreate = dateCreate;
            this.timeCreate = timeCreate;
            this.creator = creator;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDateCreate() {
            return dateCreate;
        }

        public void setDateCreate(String dateCreate) {
            this.dateCreate = dateCreate;
        }

        public String getTimeCreate() {
            return timeCreate;
        }

        public void setTimeCreate(String timeCreate) {
            this.timeCreate = timeCreate;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }
    }
    class WorkerClass{
        public String Id;
        public String name;
        public String description;

        public WorkerClass(String id, String name, String description) {
            Id = id;
            this.name = name;
            this.description = description;
        }

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
    class WorkerAdapter extends ArrayAdapter<WorkerClass>{
        public WorkerAdapter(@NonNull Context context, int resource, @NonNull List<WorkerClass> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.item_woker, parent, false);
            TextView label = (TextView) row.findViewById(R.id.textView15);
            TextView description = (TextView) row.findViewById(R.id.textView21);
            label.setText(workersList.get(position).getName());
            description.setText(workersList.get(position).getDescription());



            return row;
        }

    }
    class AttemptEditRequest extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String address = args[1];
            String session = args[0];
            String apartment=args[2];
            String direction=args[3];
            String type=args[4];
            String customer=args[5];
            String message=args[6];
            String phone=args[7];
            String note=args[8];
            String id=args[9];

            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("session", session));
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
                    +"/Request/editRequest.php","POST", params);

            return json;

        }

        protected void onPostExecute(JSONObject result) {

            try {
                if (result != null) {
                    if (result.getString("success").equals("1")){
                        finish();

                    }else {
                        Snackbar.make(addNote,"Ошибка: Код "+result.getString("cod"),Snackbar.LENGTH_LONG).show();
                    }

                } else {
                    Snackbar.make(addNote,"Ошибка сети",Snackbar.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("requst",e.toString());



            }
            //  swipeRefresh.setRefreshing(false);




        }

    }
}
