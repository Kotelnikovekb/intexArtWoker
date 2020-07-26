package ru.ekbapp.intexartwoker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.ekbapp.intexartwoker.Adapters.CounterAdapter;
import ru.ekbapp.intexartwoker.Adapters.PhotoAdapter;
import ru.ekbapp.intexartwoker.Class.CounterClass;
import ru.ekbapp.intexartwoker.Class.JsonHelper;
import ru.ekbapp.intexartwoker.Class.PackageClass;
import ru.ekbapp.intexartwoker.Class.PhotoClass;
import ru.ekbapp.intexartwoker.Class.WorkItem;
import ru.ekbapp.intexartwoker.Module.DateInputMask;

import static ru.ekbapp.intexartwoker.Class.JsonHelper.OBJECT_SEE_ACT;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.OBJECT_SET_ACT;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.OBJECT_TOTAL_ACT;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_LVL;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_P1;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_P2;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_T1;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_T2;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_T3;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_T4;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_TER;
import static ru.ekbapp.intexartwoker.RequestActivity.PRINT_ID;
import static ru.ekbapp.intexartwoker.RequestActivity.PRINT_NUMBER;
import static ru.ekbapp.intexartwoker.StartUpActivity.DOCUMENT_LIST_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.SETTINGS_S;

public class DocumerCreatorActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button addPhotoBTN;
    SharedPreferences sharedPreferences;
    String ID_REQUEST,NUMBER_REQUEST;
    File folderRequest;
    List<PhotoClass> listPhoto=new ArrayList<>();
    PhotoAdapter adapterPhoto;
    Button addWorkBTN,CreateBTN;
    ADAPTER workAdapter;
    public static final String PACKAGE_STRING = "PACKAGE_STRING";

    List<WorkItem> workItemList=new ArrayList<>();
    List<CounterClass> counterItemList=new ArrayList<>();
    ListView workLiner;
    JsonHelper jsonHelper;
    Spinner packageNumber;
    List<PackageClass> packageList=new ArrayList<>();
    LinearLayout placeCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_documer_creator);
        recyclerView=findViewById(R.id.AttachmentsRecycler);

        sharedPreferences=getSharedPreferences(SETTINGS_S,MODE_PRIVATE);
        ID_REQUEST=sharedPreferences.getString(PRINT_ID,"");
        NUMBER_REQUEST=sharedPreferences.getString(PRINT_NUMBER,"");

        folderRequest= new File(Environment.getExternalStorageDirectory() +
                File.separator+getResources().getString(R.string.folder_name)+
                File.separator+getResources().getString(R.string.folder_request)+
                File.separator+NUMBER_REQUEST
        );
        placeCard=findViewById(R.id.placeCard);

        

        packageNumber=findViewById(R.id.spinner4);
        try{

            Log.e("DocumentCreator",sharedPreferences.getString(DOCUMENT_LIST_S,"[]"));
            final JSONArray packageJson=new JSONArray(sharedPreferences.getString(DOCUMENT_LIST_S,"[]"));
            List<String> spinnerList=new ArrayList<>();

            packageList.add(
                    new PackageClass(
                            "",
                            "",
                            "",
                            "",
                            ""

                    ));
            spinnerList.add("Укажите пакет");
            for (int i=0;i<packageJson.length();i++){
                spinnerList.add(packageJson.getJSONObject(i).getString("name"));
                packageList.add(
                        new PackageClass(
                                packageJson.getJSONObject(i).getString("id"),
                                packageJson.getJSONObject(i).getString("name"),
                                packageJson.getJSONObject(i).getString("description"),
                                packageJson.getJSONObject(i).getString("Position"),
                                packageJson.getJSONObject(i).getString("documentList")

                                ));

            }



            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, spinnerList);
            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

            packageNumber.setAdapter(adapter);
            /*
            String select=jsonHelper.getValue(JsonHelper.PACKAGE_S);
            if (!select.equals("null")){
                for (int i=0;i<packageList.size();i++){
                    if (packageList.get(i).getID().equals(select)){
                        packageNumber.setSelection(i);
                    }
                }
            }

             */
            packageNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position!=0){
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString(PACKAGE_STRING,packageList.get(position).getID());
                        editor.commit();
                        showCard(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (JSONException ex){
            ex.printStackTrace();
            Log.e("DocumentCreator",ex.toString());
        }
        
        if (!folderRequest.exists()){
            folderRequest.mkdirs();
            Log.e("PERMISSION","Папки не существует!");
            Log.e("PERMISSION","Создаю папку "+folderRequest.mkdirs());
        }
        jsonHelper=new JsonHelper(new File(folderRequest,NUMBER_REQUEST+".txt"));

        workLiner=findViewById(R.id.work_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        setTitle("Закрываем: "+NUMBER_REQUEST);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (jsonHelper.getArray(JsonHelper.PHOTO_ARRAY)!=null){
            try{
                JSONArray photo=jsonHelper.getArray(JsonHelper.PHOTO_ARRAY);
                for (int i=0;i<photo.length();i++){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(photo.getString(i), options);

                    listPhoto.add(new PhotoClass(photo.getString(i),bitmap));
                }
            }catch (JSONException ex){
                ex.printStackTrace();
            }

        }
        if (jsonHelper.getArray(JsonHelper.WORKS_ARRAY)!=null){
            try{
                JSONArray work=jsonHelper.getArray(JsonHelper.WORKS_ARRAY);
                for (int i=0;i<work.length();i++){
                    JSONObject obj=work.getJSONObject(i);
                    workItemList.add(new WorkItem(
                            obj.getString(JsonHelper.WORKS_NAME),
                            obj.getString(JsonHelper.WORKS_COUNT),
                            obj.getString(JsonHelper.WORKS_ED),
                            obj.getString(JsonHelper.WORKS_PRICE),
                            obj.getString(JsonHelper.WORKS_COST)
                    ));
                }
            }catch (JSONException ex){
                ex.printStackTrace();
            }

        }

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(DocumerCreatorActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapterPhoto=new PhotoAdapter(DocumerCreatorActivity.this,listPhoto);
        recyclerView.setAdapter(adapterPhoto);

        addPhotoBTN=findViewById(R.id.button13);
        addWorkBTN=findViewById(R.id.button10);

        workAdapter=new ADAPTER(DocumerCreatorActivity.this);
        workLiner.setAdapter(workAdapter);

        CreateBTN=findViewById(R.id.button9);


        addWorkBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumerCreatorActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_add_work, viewGroup, false);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Button close =dialogView.findViewById(R.id.buttonOk);
                final EditText nameED=dialogView.findViewById(R.id.autoCompleteTextView);

                final EditText costED=dialogView.findViewById(R.id.editText6);
                final EditText priceED=dialogView.findViewById(R.id.editText5);
                final EditText countED=dialogView.findViewById(R.id.editText3);
                final EditText edED=dialogView.findViewById(R.id.editText4);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!nameED.getText().toString().equals("")){
                            workItemList.add(new WorkItem(nameED.getText().toString(),countED.getText().toString(),edED.getText().toString(),priceED.getText().toString(),costED.getText().toString()));
                            jsonHelper.addWork(new WorkItem(nameED.getText().toString(),countED.getText().toString(),edED.getText().toString(),priceED.getText().toString(),costED.getText().toString()));
                            nameED.setText("");
                            costED.setText("");
                            priceED.setText("");
                            countED.setText("");
                            edED.setText("");
                            workAdapter.notifyDataSetChanged();
                            InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edED.getWindowToken(), 0);
                            setListViewHeightBasedOnItems(workLiner);
                            alertDialog.dismiss();

                        }else{
                            Toast.makeText(DocumerCreatorActivity.this, "Укажите название", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                countED.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        double price=getNumber(priceED);
                        double count=getNumber(countED);

                        double cost=price*count;
                        cost=Math.round(cost);
                        costED.setText(cost+"");

                    }
                });
                priceED.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        double price=getNumber(priceED);
                        double count=getNumber(countED);

                        double cost=price*count;
                        cost=Math.round(cost);
                        costED.setText(cost+"");

                    }
                });

                dialogView.findViewById(R.id.button11).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        addPhotoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(DocumerCreatorActivity.this);
            }
        });



        CreateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (workItemList.size()>0){
                    Intent intent=new Intent(DocumerCreatorActivity.this,DocumentPreviewActivity.class);
                    ArrayList<WorkItem> l =new ArrayList<WorkItem>(workItemList);
                    ArrayList<String> p =new ArrayList<String>(adapterPhoto.getListPatch());
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST",(Serializable)l);
                    args.putSerializable("PHOTOLIST",(Serializable)p);
                    intent.putExtra("BUNDLE",args);
                    startActivity(intent);
                }else{
                    Snackbar.make(addPhotoBTN,"Добавте первую работу",Snackbar.LENGTH_LONG).show();
                }
            }
        });


    }
    private void selectImage(Context context) {
        final CharSequence[] options = { "Новое фото", "Выбрать из галереи","Отмена" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Укажите источник");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Новое фото")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Выбрать из галереи")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Отмена")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0://новое фото
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");

                        String filename="photo"+new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault()).format(new Date())+".png";

                        File f = new File(folderRequest, filename);
                        try {
                            f.createNewFile();
                            Bitmap bitmap = selectedImage;
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                            byte[] bitmapdata = bos.toByteArray();

                            FileOutputStream fos = new FileOutputStream(f);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();

                            listPhoto.add(new PhotoClass(f.toString(),selectedImage));
                            jsonHelper.addPhoto(f.toString());
                            adapterPhoto.notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                    break;
                case 1://галерея
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));


                                String filename="photo"+new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault()).format(new Date())+".png";

                                File f = new File(folderRequest, filename);
                                try {
                                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                                    byte[] bitmapdata = bos.toByteArray();

                                    FileOutputStream fos = new FileOutputStream(f);
                                    fos.write(bitmapdata);
                                    fos.flush();
                                    fos.close();
                                    listPhoto.add(new PhotoClass(f.toString(),bitmap));
                                    adapterPhoto.notifyDataSetChanged();
                                    jsonHelper.addPhoto(f.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                cursor.close();

                            }
                        }

                    }
                    break;
            }
        }
    }

    Double getNumber(EditText editText){
        double answer=0;
        if (!editText.getText().toString().equals("")){
            try{
                answer=Double.parseDouble(editText.getText().toString());
            }catch (NumberFormatException error){
                error.printStackTrace();
            }
        }
        return answer;
    }

    private class ADAPTER extends ArrayAdapter<WorkItem> {


        public ADAPTER(Context context) {
            super(context, R.layout.item_work, workItemList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WorkItem work = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_work, null);
            }
            TextView name=convertView.findViewById(R.id.title);
            name.setText(work.name);
            ((TextView)convertView.findViewById(R.id.date)).setText(work.cost+" руб.");
            ((TextView)convertView.findViewById(R.id.genre)).setText(work.count+" "+work.ed);
            return convertView;
        }

    }
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int)px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;

        } else {
            return false;
        }

    }

    void showCard(int select){
        if (select!=0){
            placeCard.removeAllViews();
            String[] document=packageList.get(select).getDocumentList().split("#");
            for (int i=0;i<document.length;i++){
                switch (document[i]){
                    case "Akt_vypolnennyh_rabot":
                        placeCard.addView(cardActVypolnennyhRabor());
                        break;
                    case "Akt_dopuska_uzla_ucheta_k_ekspluatacii":
                        placeCard.addView(cardAkt_dopuska_uzla_ucheta_k_ekspluatacii());
                        break;
                    case "Akt_obsledovaniya":
                        placeCard.addView(cardAkt_obsledovaniya());
                        break;
                    case "Akt_zamera_temperatury_GVS":
                        placeCard.addView(cardAkt_GVS());
                        break;
                }
            }
        }
    }
    CardView cardActVypolnennyhRabor(){
        final LinearLayout.LayoutParams lparams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final CardView cardView = new CardView(this);
        final LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setLayoutParams(lparams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView Title=new TextView(this);
        Title.setLayoutParams(lparams);
        Title.setText("Акт выполненых работ");
        Title.setTextColor(getResources().getColor(R.color.blac));
        Title.setTextSize(18f);
        Title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(Title);
        //лист работ
        final ListView listWork=new ListView(this);
        listWork.setLayoutParams(lparams);
        listWork.setAdapter(workAdapter);
        linearLayout.addView(listWork);

        ///кнопка добавить работы

        Button addWork=new Button(this);
        addWork.setText("Добавить работу");
        addWork.setBackgroundColor(getResources().getColor(R.color.white));
        addWork.setTextColor(getResources().getColor(R.color.colorPrimary));

        addWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumerCreatorActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_add_work, viewGroup, false);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Button close =dialogView.findViewById(R.id.buttonOk);
                final EditText nameED=dialogView.findViewById(R.id.autoCompleteTextView);

                final EditText costED=dialogView.findViewById(R.id.editText6);
                final EditText priceED=dialogView.findViewById(R.id.editText5);
                final EditText countED=dialogView.findViewById(R.id.editText3);
                final EditText edED=dialogView.findViewById(R.id.editText4);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!nameED.getText().toString().equals("")){
                            workItemList.add(new WorkItem(nameED.getText().toString(),countED.getText().toString(),edED.getText().toString(),priceED.getText().toString(),costED.getText().toString()));
                            jsonHelper.addWork(new WorkItem(nameED.getText().toString(),countED.getText().toString(),edED.getText().toString(),priceED.getText().toString(),costED.getText().toString()));
                            nameED.setText("");
                            costED.setText("");
                            priceED.setText("");
                            countED.setText("");
                            edED.setText("");
                            workAdapter.notifyDataSetChanged();
                            InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Activity.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edED.getWindowToken(), 0);
                            setListViewHeightBasedOnItems(listWork);
                            alertDialog.dismiss();

                        }else{
                            Toast.makeText(DocumerCreatorActivity.this, "Укажите название", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                countED.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        double price=getNumber(priceED);
                        double count=getNumber(countED);

                        double cost=price*count;
                        cost=Math.round(cost);
                        costED.setText(cost+"");

                    }
                });
                priceED.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        double price=getNumber(priceED);
                        double count=getNumber(countED);

                        double cost=price*count;
                        cost=Math.round(cost);
                        costED.setText(cost+"");

                    }
                });

                dialogView.findViewById(R.id.button11).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        linearLayout.addView(addWork);
        cardView.addView(linearLayout);

        return cardView;
    }
    CardView cardAkt_dopuska_uzla_ucheta_k_ekspluatacii(){

        final LinearLayout.LayoutParams lparams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final CardView cardView = new CardView(this);
        final LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setLayoutParams(lparams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        View border=new View(this);
        border.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,30));
        border.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        linearLayout.addView(border);
        TextView Title=new TextView(this);
        Title.setLayoutParams(lparams);
        Title.setText("Акт о снятии контрольных показаний водомерных счетчиков");
        Title.setTextColor(getResources().getColor(R.color.blac));
        Title.setTextSize(18f);
        Title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(Title);
        //Лист счетчиков
        final List<CounterClass> counterClassList=new ArrayList<>();
        final CounterAdapter adapter=new CounterAdapter(DocumerCreatorActivity.this,counterClassList);
        final ListView listViewCounter=new ListView(this);
        listViewCounter.setLayoutParams(lparams);
        listViewCounter.setAdapter(adapter);
        linearLayout.addView(listViewCounter);
        //добавить счетчик
        //new DateInputMask(pok_nxk);
        Button addWork=new Button(this);
        addWork.setText("Добавить cчетчик");
        addWork.setBackgroundColor(getResources().getColor(R.color.white));
        addWork.setTextColor(getResources().getColor(R.color.colorPrimary));

        addWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumerCreatorActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_add_counter, viewGroup, false);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Button close =dialogView.findViewById(R.id.buttonOk2);
                final EditText dateInput=dialogView.findViewById(R.id.dateCounter);
                final EditText IndicationsInput=dialogView.findViewById(R.id.IndicationsCounter);
                final EditText numberInput=dialogView.findViewById(R.id.numberCounter);
                final EditText pealInput=dialogView.findViewById(R.id.pealCounter);
                final Spinner type=dialogView.findViewById(R.id.spinner5);
                final Spinner position=dialogView.findViewById(R.id.spinner6);
                new DateInputMask(dateInput);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        counterClassList.add(new CounterClass(
                                position.getSelectedItem().toString(),
                                type.getSelectedItem().toString(),
                                numberInput.getText().toString(),
                                dateInput.getText().toString(),
                                pealInput.getText().toString(),
                                IndicationsInput.getText().toString()
                        ));
                        jsonHelper.addCounter(new CounterClass(
                                position.getSelectedItem().toString(),
                                type.getSelectedItem().toString(),
                                numberInput.getText().toString(),
                                dateInput.getText().toString(),
                                pealInput.getText().toString(),
                                IndicationsInput.getText().toString()
                        ));
                        adapter.notifyDataSetChanged();
                        InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(listViewCounter.getWindowToken(), 0);
                        setListViewHeightBasedOnItems(listViewCounter);
                        alertDialog.dismiss();
                    }
                });




                dialogView.findViewById(R.id.CalneBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        linearLayout.addView(addWork);
        cardView.addView(linearLayout);

        return cardView;

    }
    CardView cardAkt_obsledovaniya(){
        final LinearLayout.LayoutParams lparams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final CardView cardView = new CardView(this);
        final LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setLayoutParams(lparams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        View border=new View(this);
        border.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,30));
        border.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        linearLayout.addView(border);
        TextView Title=new TextView(this);
        Title.setLayoutParams(lparams);
        Title.setText("Акт обследования");
        Title.setTextColor(getResources().getColor(R.color.blac));
        Title.setTextSize(18f);
        Title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(Title);

        TextInputLayout l1=new TextInputLayout(this);
        l1.setLayoutParams(lparams);
        final EditText objectSee=new EditText(this);
        objectSee.setLayoutParams(lparams);
        objectSee.setHint("Предмет обследования");
        l1.addView(objectSee);
        linearLayout.addView(l1);


        TextInputLayout l2=new TextInputLayout(this);
        l2.setLayoutParams(lparams);
        final EditText objectSet=new EditText(this);
        objectSet.setLayoutParams(lparams);
        objectSet.setHint("Установила следующее:");
        l2.addView(objectSet);
        linearLayout.addView(l2);

        TextInputLayout l3=new TextInputLayout(this);
        l3.setLayoutParams(lparams);
        final EditText objectTotal=new EditText(this);
        objectTotal.setLayoutParams(lparams);
        objectTotal.setHint("Выводы:");
        l3.addView(objectTotal);
        linearLayout.addView(l3);

        objectSee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!objectSee.getText().toString().equals("")){
                    jsonHelper.setParametr(OBJECT_SEE_ACT,objectSee.getText().toString());
                }
            }
        });

        objectSet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!objectSet.getText().toString().equals("")){
                    jsonHelper.setParametr(OBJECT_SET_ACT,objectSet.getText().toString());
                }
            }
        });

        objectTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!objectTotal.getText().toString().equals("")){
                    jsonHelper.setParametr(OBJECT_TOTAL_ACT,objectTotal.getText().toString());
                }
            }
        });


        cardView.addView(linearLayout);
        return cardView;
    }
    //Акт
    //замера температуры ГВС
    CardView cardAkt_GVS(){
        final LinearLayout.LayoutParams lparams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final CardView cardView = new CardView(this);
        final LinearLayout linearLayout=new LinearLayout(this);
        linearLayout.setLayoutParams(lparams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        View border=new View(this);
        border.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,30));
        border.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        linearLayout.addView(border);
        TextView Title=new TextView(this);
        Title.setLayoutParams(lparams);
        Title.setText("Акт замера температуры ГВС");
        Title.setTextColor(getResources().getColor(R.color.blac));
        Title.setTextSize(18f);
        Title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(Title);

        linearLayout.addView(createTextInput(this,"Этаж:",ZAMER_LVL));
        linearLayout.addView(createTextInput(this,"Температура на вводе Т1 \"прямая\" (град.С):",ZAMER_T1));
        linearLayout.addView(createTextInput(this,"Температура на вводе Т2 \"обратка\" (град.С):",ZAMER_T2));
        linearLayout.addView(createTextInput(this,"Температура ГВС после бойлера Т3 (град.С):",ZAMER_T3));
        linearLayout.addView(createTextInput(this,"Температура ГВС на циркуляции перед бойлером Т4:",ZAMER_T4));
        linearLayout.addView(createTextInput(this,"Давление на вводе: Р1 \"прямая\" (бар):",ZAMER_P1));
        linearLayout.addView(createTextInput(this,"Давление на вводе: Р2 \"обратка\" (бар):",ZAMER_P2));
        linearLayout.addView(createTextInput(this,"Замеры проводились термометром:",ZAMER_TER));

        linearLayout.addView(createTextInput(this,"Температура кухня:",ZAMER_LVL));
        linearLayout.addView(createTextInput(this,"Примечание кухня:",ZAMER_LVL));

        linearLayout.addView(createTextInput(this,"Этаж:",ZAMER_LVL));
        linearLayout.addView(createTextInput(this,"Этаж:",ZAMER_LVL));








        cardView.addView(linearLayout);

        return cardView;
    }
    TextInputLayout createTextInput(Context context, String hint, final String key){
        final LinearLayout.LayoutParams lparams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextInputLayout textInputLayout=new TextInputLayout(context);
        textInputLayout.setLayoutParams(lparams);
        final EditText objectSee=new EditText(this);
        objectSee.setLayoutParams(lparams);
        objectSee.setHint(hint);


        objectSee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!objectSee.getText().toString().equals("")){
                    jsonHelper.setParametr(key,objectSee.getText().toString());
                }
            }
        });
        textInputLayout.addView(objectSee);

        return textInputLayout;
    }
}
