package ru.ekbapp.intexartwoker;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import ru.ekbapp.intexartwoker.Class.JsonHelper;
import ru.ekbapp.intexartwoker.Class.WorkItem;
import ru.ekbapp.intexartwoker.Module.DatabaseHelper;
import ru.ekbapp.intexartwoker.Module.Money;
import ru.ekbapp.intexartwoker.Module.Share_void;
import ru.ekbapp.intexartwoker.Views.DrawView;

import static ru.ekbapp.intexartwoker.Class.JsonHelper.COMPANY_NAME;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.COUNTER_DATE;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.COUNTER_INDICATION;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.COUNTER_NUMBER;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.COUNTER_PEAL;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.COUNTER_POSITION;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.COUNTER_TYPE;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.CUSTOMER_NAME;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.EXECUTOR_NAME_ABLATIVE;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.EXECUTOR_ROLE;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.OBJECT_SET_ACT;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.OBJECT_TOTAL_ACT;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_K_P;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_K_T;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_LVL;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_P1;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_P2;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_S_P;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_S_T;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_T1;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_T2;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_T3;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_T4;
import static ru.ekbapp.intexartwoker.Class.JsonHelper.ZAMER_TER;
import static ru.ekbapp.intexartwoker.DocumerCreatorActivity.PACKAGE_STRING;
import static ru.ekbapp.intexartwoker.RequestActivity.PRINT_ID;
import static ru.ekbapp.intexartwoker.RequestActivity.PRINT_NUMBER;
import static ru.ekbapp.intexartwoker.StartUpActivity.DOCUMENT_LIST_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.SETTINGS_S;
import static ru.ekbapp.intexartwoker.StartUpActivity.USER_NAME_S;

public class DocumentPreviewActivity extends AppCompatActivity {

    Button addWorkBTN;

    PDFView pdfView;

    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    public static final String FONT1 = "/assets/fonts/12136.ttf";
    boolean signatureDone=false;
    int kl=100;
    ArrayList<WorkItem> WorkList;
    ArrayList<String> photoList;
    JsonHelper jsonHelper;
    File docsFolder;
    Font font8;


    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_preview);
        sharedPreferences=getSharedPreferences(SETTINGS_S,MODE_PRIVATE);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        WorkList = (ArrayList<WorkItem>) args.getSerializable("ARRAYLIST");
        photoList = (ArrayList<String>) args.getSerializable("PHOTOLIST");

        docsFolder = new File(Environment.getExternalStorageDirectory() +
                File.separator+getResources().getString(R.string.folder_name)+
                File.separator+getResources().getString(R.string.folder_request)+
                File.separator+sharedPreferences.getString(PRINT_NUMBER,""));

        final File JSONREQUEST = new File(docsFolder, sharedPreferences.getString(PRINT_NUMBER,"")+".txt");
        jsonHelper=new JsonHelper(JSONREQUEST);

        progressDialog=new ProgressDialog(DocumentPreviewActivity.this);
        progressDialog.setTitle("Формирую документ");
        progressDialog.setCancelable(false);
        progressDialog.show();
        pdfView=findViewById(R.id.pdf_v);

        sqlHelper = new DatabaseHelper(DocumentPreviewActivity.this);
        db = sqlHelper.open();

        addWorkBTN=findViewById(R.id.button12);
        addWorkBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signatureDone){
                    ContentValues cv = new ContentValues();
                    cv.put(DatabaseHelper.COLUMN_NEW_REQ_NUMBER,sharedPreferences.getString(PRINT_NUMBER,""));
                    cv.put(DatabaseHelper.COLUMN_ID_REQ,sharedPreferences.getString(PRINT_ID,""));
                    cv.put(DatabaseHelper.COLUMN_NEW_REQ_STATUS,0);
                    db.insert(DatabaseHelper.COLUMN_NAME, null, cv);

                    startActivity(new Intent(DocumentPreviewActivity.this,MainActivity.class));
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(DocumentPreviewActivity.this);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_signature, viewGroup, false);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    final DrawView drawView=dialogView.findViewById(R.id.drawView);


                    dialogView.findViewById(R.id.button11).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    dialogView.findViewById(R.id.buttonClear).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawView.clear();

                        }
                    });
                    dialogView.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            File file = new File(docsFolder,    Share_void.md5(jsonHelper.getValue(JsonHelper.CUSTOMER_NAME)) +".png");
                            try {
                                FileOutputStream ostream = new FileOutputStream(file);
                                Bitmap well = drawView.getBitmap();
                                Bitmap save = Bitmap.createBitmap(320, 480,
                                        Bitmap.Config.ARGB_8888);
                                Paint paint = new Paint();
                                paint.setColor(Color.WHITE);
                                Canvas now = new Canvas(save);
                                now.drawRect(new Rect(0, 0, 320, 480), paint);
                                now.drawBitmap(well,
                                        new Rect(0, 0, well.getWidth(), well.getHeight()),
                                        new Rect(0, 0, 320, 480), null);

                                save.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                signatureDone=true;
                                new createDocunent().execute();

                                progressDialog.show();
                                addWorkBTN.setText("Закрыть завку");

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Snackbar.make(dialogView,"Ошибка: "+e.toString(),Snackbar.LENGTH_LONG).show();
                            }


                            alertDialog.dismiss();

                        }
                    });
                    alertDialog.setCanceledOnTouchOutside(false);

                }

            }
        });

        new createDocunent().execute();
        progressDialog.setMessage("Создаю документ");


    }
    class createDocunent extends AsyncTask<String,String,String> {

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected String doInBackground(String... strings) {
            Document document = new Document();

            File docsFolder = new File(Environment.getExternalStorageDirectory() +
                    File.separator+getResources().getString(R.string.folder_name)+
                    File.separator+getResources().getString(R.string.folder_request)+
                    File.separator+sharedPreferences.getString(PRINT_NUMBER,""));

            if (!docsFolder.exists()) {
                docsFolder.mkdir();

            }

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
            Date date2=new Date();
            String tooday=dateFormat.format(date2);
            Log.e("PRINT", "Печатаю заявку "+sharedPreferences.getString(PRINT_ID,""));

            String name_doc=(tooday+"nomer"+ sharedPreferences.getString(PRINT_ID,""));
            File pdfFile = new File(docsFolder, name_doc+".pdf");



            Image image_a;
            try {
                OutputStream output = new FileOutputStream(pdfFile);
                publishProgress("Открываю поток");
                PdfWriter.getInstance(document, output);
                document.setMargins(36, 36, 10, 0);
                document.setMarginMirroringTopBottom(true);

                document.setPageSize(PageSize.A4);
                BaseFont bf = BaseFont.createFont(FONT1, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                font8 = new Font(bf, 7);
                Font font_bolt8 = new Font(bf, 10,Font.BOLD);
                Font font = new Font(bf, 10);
                Font font_bolt = new Font(bf, 10,Font.BOLD);
                Font font_bolt_s = new Font(bf, 10,Font.BOLDITALIC);


                document.addCreator("ИНТЕХАРТ");
                document.open();
                publishProgress("Открываю Документ");

                //Шапка с датой
                PdfPTable tableData=new PdfPTable(new float[] { 1, 1 });
                PdfPCell cellDate1 = new PdfPCell(new Paragraph(jsonHelper.getValue(JsonHelper.CITY),font));
                PdfPCell cellDate2 = new PdfPCell(new Paragraph(tooday,font));
                cellDate1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellDate2.setHorizontalAlignment(Element.ALIGN_RIGHT);

                cellDate1.setBorder(PdfPCell.NO_BORDER);
                cellDate2.setBorder(PdfPCell.NO_BORDER);

                tableData.addCell(cellDate1);
                tableData.addCell(cellDate2);
                tableData.setWidthPercentage(100f);


                Log.e("json","Подготовка таблицы подписей");
                PdfPTable table_signature = new PdfPTable(new float[] { 1, 1 });
                table_signature.getDefaultCell().setHorizontalAlignment(Element.ALIGN_TOP);
                PdfPCell name =new PdfPCell(new Paragraph("Подпись представителя УК\n"+sharedPreferences.getString(USER_NAME_S,""),font));
                image_a= Image.getInstance(sharedPreferences.getString(StartUpActivity.USER_SIGNATURE_DEVICE_S,""));
                Log.e("json","Подпись исполнителя найденв");
                PdfPCell cell3=new PdfPCell();
                cell3.setImage(image_a);
                Image img;

                if (signatureDone){
                    img = Image.getInstance(String.valueOf(new File(docsFolder, Share_void.md5(jsonHelper.getValue(JsonHelper.CUSTOMER_NAME))+".png")));
                }else {
                    Drawable signature_null_image = getResources().getDrawable(R.drawable.image);
                    BitmapDrawable bit_signature_null = ((BitmapDrawable) signature_null_image);
                    Bitmap bmp_null = bit_signature_null.getBitmap();
                    ByteArrayOutputStream stream_null = new ByteArrayOutputStream();
                    bmp_null.compress(Bitmap.CompressFormat.PNG, 100, stream_null);
                    img = Image.getInstance(stream_null.toByteArray());
                    img.scaleAbsolute(500,100);
                }



                /*
                поля
                 */




                img.scaleAbsolute(kl,kl);
                img.setAlignment(Element.ALIGN_RIGHT);
                PdfPCell cell4 = new PdfPCell(img);
                name.setHorizontalAlignment(Element.ALIGN_CENTER);
                name.setBorder(PdfPCell.NO_BORDER);
                PdfPCell client =new PdfPCell(new Paragraph("Подпись собственника (потребитель)\n"+jsonHelper.getValue(JsonHelper.CUSTOMER_NAME),font));
                client.setHorizontalAlignment(Element.ALIGN_CENTER);
                client.setBorder(PdfPCell.NO_BORDER);

                Log.e("json","Таблица подписей создана");

                cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                //cell3.setPadding(pad);
                //  cell4.setPadding(pad);
                cell3.setFixedHeight(40f);
                // cell3.setCellEvent(new WatermarkedCell(Detal_request_Activity.executor));
                // cell4.setCellEvent(new WatermarkedCell(opd_t_fio_s));
                cell3.setBorder(PdfPCell.NO_BORDER);
                cell4.setBorder(PdfPCell.NO_BORDER);
                // cell3.setBackgroundColor(BaseColor.BLUE);
                //cell4.setBackgroundColor(BaseColor.BLUE);
                table_signature.addCell(name);
                table_signature.addCell(client);
                table_signature.addCell(cell3);
                table_signature.addCell(cell4);
                table_signature.setWidthPercentage(100f);

                publishProgress("Готовлю подписи");
                document.add(new Paragraph("\n\n\n"));
                PdfPTable table_header = new PdfPTable(new float[] { 1, 1 });
                table_header.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);


                Paragraph docTitle = new Paragraph("", font8);
                Paragraph doc = new Paragraph(jsonHelper.getValue(JsonHelper.COMPANY_NAME), font);
                table_header.setWidthPercentage(100);


                PdfPCell cell = new PdfPCell(docTitle);
                PdfPCell cell2 = new PdfPCell(doc);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);

                cell.setBorder(PdfPCell.NO_BORDER);
                cell2.setBorder(PdfPCell.NO_BORDER);

                table_header.addCell(cell);
                table_header.addCell(cell2);
                table_header.setWidthPercentage(100);

                final JSONArray packageJson=new JSONArray(sharedPreferences.getString(DOCUMENT_LIST_S,"[]"));
                String selectedId=sharedPreferences.getString(PACKAGE_STRING,"");
                String documentListString="";
                for (int i=0;i<packageJson.length();i++){
                    JSONObject obj=packageJson.getJSONObject(i);
                    if (obj.getString("id").equals(selectedId)){
                        documentListString=obj.getString("documentList");
                    }
                }

                String[] documentList=documentListString.split("#");

                //акт приемки
                if (Arrays.asList(documentList).contains("Akt_vypolnennyh_rabot")){
                    Paragraph p1=new Paragraph("АКТ № "+sharedPreferences.getString(PRINT_NUMBER,"")+"\nприемки оказанных услуг и выполненных работ по содержанию\n" +
                            "и текущему ремонту общего имущества в многоквартирном доме\n",font);
                    p1.setAlignment(Element.ALIGN_CENTER);
                    document.add(p1);

                    document.add(tableData);




                    document.add(new Paragraph("\n             Собственники помещений в многоквартирном доме, расположенном по адресу: " +jsonHelper.getValue(JsonHelper.STREET)+
                            ", являющегося собственником квартиры № "+jsonHelper.getValue(JsonHelper.APARTMENT)+", действующего на основании  именуемый в дальнейшем «Заказчик», в лице "+jsonHelper.getValue(JsonHelper.CUSTOMER_NAME)+" с одной стороны, и "+jsonHelper.getValue(JsonHelper.COMPANY_NAME)+", именуемое в дальнейшем «Исполнитель», в лице "+jsonHelper.getValue(EXECUTOR_ROLE)+" "+jsonHelper.getValue(JsonHelper.EXECUTOR_NAME_GENITIVE)+"," +
                            " действующего на основании "+jsonHelper.getValue(JsonHelper.BASE)+", с другой стороны, совместно именуемые «Стороны», по начальной заявке \""+jsonHelper.getValue(JsonHelper.MESSAGE_REQUEST)+"\" составили настоящий Акт о нижеследующем:",font));
                    document.add(new Paragraph("1. Исполнителем предъявлены к приемке следующие оказанные на основании договора оказания услуг по содержанию и выполнению работ по" +
                            " ремонту общего имущества в многоквартирном доме № "+jsonHelper.getValue(JsonHelper.ADDRESS_DOGOVOR_NUMBER)+" от "+jsonHelper.getValue(JsonHelper.ADDRESS_DOGOVOR_DATE)+". (далее - «Договор») услуги и выполненные работы по содержанию и текущему ремонту общего имущества в" +
                            " многоквартирном доме, расположенном по адресу: "+jsonHelper.getValue(JsonHelper.STREET)+" кв "+jsonHelper.getValue(JsonHelper.APARTMENT),font));

                    document.add(new Paragraph("\n  \n",font));
                    PdfPTable table1 = new PdfPTable(new float[] { 3,2,2,2,2 });
                    table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table1.setWidthPercentage(100);
                    table1.addCell(new PdfPCell(new Paragraph("Наименование вида работы (услугу)²",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Количественный показатель выполненной работы (оказанной услуги)",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Еденица измерения работы (услуги)",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Стоимость³/ сметная стоимость, выполненной работы (оказанной услуги) за еденицу",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Цена выполненной работы (оказанной услуги), в рублях",font)));

                    double cost=0;

                    for(int i=0;i<WorkList.size();i++){
                        table1.addCell(new PdfPCell(new Paragraph(WorkList.get(i).getName(),font)));
                        table1.addCell(new PdfPCell(new Paragraph(WorkList.get(i).getCount(),font)));
                        table1.addCell(new PdfPCell(new Paragraph(WorkList.get(i).getEd(),font)));
                        table1.addCell(new PdfPCell(new Paragraph(WorkList.get(i).getPrice(),font)));
                        table1.addCell(new PdfPCell(new Paragraph(WorkList.get(i).getCost(),font)));
                        if (!WorkList.get(i).getCost().equals("")){
                            try{
                                cost+=Double.parseDouble(WorkList.get(i).getCost());
                            }catch (NumberFormatException error){
                                error.printStackTrace();

                                Log.e("PRINT", error.toString());
                            }
                        }
                    }


                    document.add(table1);

                    Log.e("json","step 2");




                    String cost_string= Money.inwords(cost);

                    String cost_string_lite= String.format("%.0f",cost) +" руб. 00";
                    document.add(new Paragraph("2. Всего за период с «____»_________2020г. По «____»________2020г. выполнено работ (оказано услуг) на общую сумму "+ cost_string_lite+"  коп.( "+cost_string+" ).\n" +
                            "3. Работы (услуги) выполнены (оказаны) полностью, в установленные сроки, с надлежащим качеством.\n" +
                            "4. Претензий по выполнению условий Договора Стороны друг к другу не имеют.\n" +
                            "Настоящий акт составлен в 2-х экземплярах, имеющих одинаковую юридическую силу, по одному для каждой из Сторон.\n",font));
                    Paragraph paragraph=new Paragraph("\nПодписи Сторон:",font);
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    document.add(paragraph);
                    document.add(new Paragraph("\n " ));

                    document.add(table_signature);

                    document.add(new Paragraph("Примечания:\n" +
                            "1. В соответствии с пунктом 4 части 8 статьи 161.1 Жилищного кодекса Российской Федерации (Собрание законодательства Российской Федерации, 2005, №1, ст. 14; 2011, №23, ст. 3263; 2014, №30, ст. 4264; 2015, №27, ст. 3967) председатель совета многоквартирного дома подписывает в том числе акты приемки оказанных услуг и (или) выполненных работ по содержанию и текущему ремонту общего имущества в многоквартирном доме.\n" +
                            "2. Минимальный перечень услуг и работ, необходимых для обеспечения надлежащего содержания общего имущества в многоквартирном доме, утвержден постановлением Правительства Российской Федерации от 3 апреля 2013г. №290\n" +
                            "3. Стоимость за еденицу выполненной работы (оказанной услуги) по договору управления многоквартирным домом или договору оказания услуг по содержанию и (или) выполнению работ по ремонту общего имущества в многоквартирном доме.\n" +
                            "4. Сметная стоимость за единицу выполненной работы по договору подряда по выполнению работ по ремонту общего имущества в многоквартирном доме.\n",font8));
                    document.newPage();
                }
                if (Arrays.asList(documentList).contains("Akt_dopuska_uzla_ucheta_k_ekspluatacii")){
                    document.add(application(3));
                    document.add(table_header);

                    Paragraph p3 = new Paragraph("\nАкт\n" +
                            "о снятии контрольных показаний водомерных счетчиков\n", font_bolt);
                    p3.setAlignment(Element.ALIGN_CENTER);

                    document.add(p3);
                    Paragraph p4=new Paragraph("Дата снятия показаний: "+tooday,font);
                    p4.setAlignment(Element.ALIGN_LEFT);
                    document.add(p4);

                    Paragraph p5=new Paragraph("\nСоставлен настоящий Акт представителем "+jsonHelper.getValue(JsonHelper.COMPANY_NAME)+" "+jsonHelper.getValue(JsonHelper.EXECUTOR_NAME_ABLATIVE)+"\n" +
                            "в присутствии потребителя "+ jsonHelper.getValue(JsonHelper.CUSTOMER_NAME)+" тел. "+ jsonHelper.getValue(JsonHelper.CUSTOMER_PHONE) +"\n" +
                            "в том, что сего числа сняты показания водомерного счетчика расхода горячей и холодной воды по адресу: "+jsonHelper.getValue(JsonHelper.STREET)+
                            " кв "+jsonHelper.getValue(JsonHelper.APARTMENT),font);
                    p5.setAlignment(Element.ALIGN_LEFT);
                    document.add(p5);
                    document.add(new Paragraph("\n "));

                    PdfPTable table1 = new PdfPTable(new float[] { 1, 2,3,3,2,2,2 });
                    table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table1.setWidthPercentage(100);
                    table1.addCell(new PdfPCell(new Paragraph("№",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Тип воды",font)));
                    table1.addCell(new PdfPCell(new Paragraph("№ счетчика",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Дата следующей поверки",font)));
                    table1.addCell(new PdfPCell(new Paragraph("№ пломбы",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Показания",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Помещения",font)));

                    JSONArray arrayCounter=jsonHelper.getArray(JsonHelper.COUNTER_ARRAY);
                    for (int i=0;i<arrayCounter.length();i++){
                        JSONObject obj=arrayCounter.getJSONObject(i);

                        int j=i+1;
                        table1.addCell(new PdfPCell(new Paragraph(j+"",font)));
                        table1.addCell(new PdfPCell(new Paragraph(obj.getString(COUNTER_TYPE),font)));
                        table1.addCell(new PdfPCell(new Paragraph(obj.getString(COUNTER_NUMBER),font)));
                        table1.addCell(new PdfPCell(new Paragraph(obj.getString(COUNTER_DATE),font)));
                        table1.addCell(new PdfPCell(new Paragraph(obj.getString(COUNTER_PEAL),font)));
                        table1.addCell(new PdfPCell(new Paragraph(obj.getString(COUNTER_INDICATION),font)));
                        table1.addCell(new PdfPCell(new Paragraph(obj.getString(COUNTER_POSITION),font)));
                    }

                    document.add(table1);

                    Paragraph p6=new Paragraph("\nПримечание:\n" +
                            "В случае выхода из строя или утраты ранее введенного в эксплуатацию индивидуального прибора учёта либо истечения срока эксплуатации, определяемого" +
                            " периодом времени до очередной поверки расчёты производятся по среднему с «___»_______2018г., но не более 3 расчетных периодов подряд (Постановление " +
                            "Правительства РФ №354 от 06.05.11г. «Опредоставлении коммунальных услуг собственникам и пользователям помещений в многоквартирных домах и жилых домов» п.59)." +
                            " По истечении предельного количества расчетных периодов плата за коммунальную услугу рассчитывается исходя из нормативов потребления коммунальных услуг п. 60 " +
                            "«Правил предоставления коммунальных услуг».\n" +
                            "При обнаружении исполнителем факта несанкционированного вмешательства в работу индивидуального, общего, комнатного прибора учёта начисление " +
                            "производиться по нормативам потребления коммунальных услуг п.62 «Правил предоставления коммунальных услуг».\n",font8);
                    //p6.setAlignment(Element.ALIGN_LEFT);
                    document.add(p6);





                    document.add(new Paragraph("\n " ));

                    document.add(table_signature);
                    document.newPage();
                }
                //Акт замены счетчиков холодной воды в помещении кухня
                //Согласие на обработку персональных данных
                if (Arrays.asList(documentList).contains("PD")){
                    document.add(application(4));
                    Paragraph p2=new Paragraph("\n\n \n \nСогласие на обработку персональных данных",font_bolt);
                    p2.setAlignment(Element.ALIGN_CENTER);
                    document.add(p2);

                    Paragraph text_long=new Paragraph("",font);
                    text_long.add(new Chunk("В соответствии с Федеральным Законом от 27.07.2006 №152-ФЗ «О персональных данных» я, ",font_bolt));
                    text_long.add(new Chunk(jsonHelper.getValue(JsonHelper.CUSTOMER_NAME)+" собственник " +
                            "(наниматель) жилого помещения, расположенного по адресу: "+jsonHelper.getValue(JsonHelper.STREET)+" кв "+jsonHelper.getValue(JsonHelper.APARTMENT),font));
                    text_long.add(new Chunk(" выражаю согласие ",font_bolt));

                    text_long.add(new Chunk("на осуществлением Исполнителем "+jsonHelper.getValue(JsonHelper.COMPANY_NAME)+" и ОАО  «ЕИРКЦ» со всеми персональными данными указанными мною в " +
                            "настоящем Заявлении, а также  содержащимися в предоставленных мною документах, следующих действий: сбор, систематизация, накопление, хранение" +
                            " ;уничтожение обновление; изменение; использование; расторжение, обмен; обезличивание (далее — обработка), для целей начисления, сбора," +
                            " взыскания платы за поставляемые ресурсы (услуги).\n" +
                            "              Исполнитель "+jsonHelper.getValue(JsonHelper.COMPANY_NAME)+" и ООО «ЕИРКЦ» вправе осуществлять смешанную (автоматизированную и не автоматизированную) " +
                            "обработку  персональных данных  посредством внесения  их в базы  данных на бумажном или электронном носителе, включения в списки " +
                            "(реестры и отчетные формы), в том числе в системе Интернет.\n" +
                            "              Настоящее согласие действует бессрочно.\n" +
                            "              Я оставляю за собой  право отозвать свое соглание  посредством составления соответственного письменного документа," +
                            " который может быть направлен   мной в адрес Исполнителя "+jsonHelper.getValue(JsonHelper.COMPANY_NAME)+" по почте заказным письмом с уведомлением о вручении.\n",font));


                    document.add(text_long);


                    Paragraph p3=new Paragraph("",font);

                    p3.setAlignment(Element.ALIGN_LEFT);
                    document.add(p3);

                    Paragraph aft=new Paragraph("Подпись потребителя:",font);
                    aft.setAlignment(Element.ALIGN_LEFT);
                    document.add(aft);

                    document.add(new Paragraph("\n " ));

                    PdfPTable table2 = new PdfPTable(new float[] { 1, 1 });
                    table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_TOP);



                    //PdfPCell cell3=new PdfPCell();
                    table2.setWidthPercentage(100);

                    cell3.setImage(image_a);



                    document.add(img);
                    document.add(new Paragraph(tooday,font));
                    //table2.addCell(img);
                    document.newPage();
                }
                //Акт обследования
                if (Arrays.asList(documentList).contains("Akt_obsledovaniya")){
                    Paragraph p1=new Paragraph("Акт обследования",font_bolt);
                    p1.setAlignment(Element.ALIGN_CENTER);
                    document.add(p1);
                    document.add(new Paragraph(tooday,font));

                    document.add(new Paragraph("Комиссия в составе:\nПредставители "+jsonHelper.getValue(COMPANY_NAME)+" "+sharedPreferences.getString(USER_NAME_S,"")+"\n" +
                            "Представитель собственника "+jsonHelper.getValue(CUSTOMER_NAME),font));
                    document.add(new Paragraph("Обследовала:",font_bolt));
                    document.add(new Paragraph(jsonHelper.getValue(JsonHelper.OBJECT_SEE_ACT)+"\n\n",font));
                    document.add(new Paragraph("Установила следующее:",font_bolt));
                    document.add(new Paragraph(jsonHelper.getValue(OBJECT_SET_ACT)+"\n\n",font));
                    document.add(new Paragraph("Выводы:",font_bolt));
                    document.add(new Paragraph(jsonHelper.getValue(OBJECT_TOTAL_ACT)+"\n\n\n",font));



                    document.add(table_signature);
                    document.newPage();
                }

                //Акт
                //замера температуры ГВС
                if (Arrays.asList(documentList).contains("Akt_zamera_temperatury_GVS")){
                    Paragraph paragraph=new Paragraph("Акт  \nзамера температуры ГВС",font_bolt);
                    paragraph.setAlignment(Element.ALIGN_CENTER);
                    document.add(paragraph);
                    document.add(new Paragraph("\n"+tooday,font_bolt));

                    document.add(new Paragraph("\nКомиссия в составе: Представители "+jsonHelper.getValue(COMPANY_NAME)+" "+jsonHelper.getValue(EXECUTOR_NAME_ABLATIVE)+"\nПредставитель собственника: "+jsonHelper.getValue(CUSTOMER_NAME)+"\n"+
                            "Провели замеры температуры ГВС по адресу: "+jsonHelper.getValue(JsonHelper.STREET)+" кв "+jsonHelper.getValue(JsonHelper.APARTMENT),font));

                    document.add(new Paragraph("\n\nВводные данные:",font_bolt));
                    document.add(new Paragraph("Температура на вводе Т1 \"прямая\" (град.С): "+jsonHelper.getValue(ZAMER_T1)+
                            "\nТемпература на вводе Т2 \"обратка\" (град.С): "+jsonHelper.getValue(ZAMER_T2)+"\nДавление на вводе: Р1 \"прямая\" (бар): "+jsonHelper.getValue(ZAMER_P1)+"\nДавление на вводе: Р2 \"обратка\" (бар): "+jsonHelper.getValue(ZAMER_P2)+
                            "\nТемпература ГВС после бойлера Т3 (град.С) "+jsonHelper.getValue(ZAMER_T3)+
                            "\nТемпература ГВС на циркуляции перед бойлером Т4: "+jsonHelper.getValue(ZAMER_T4)+
                            "\nЗамеры температуры внутри жилого помещения:",font));

                    document.add(new Paragraph("\n "));

                    PdfPTable table1 = new PdfPTable(new float[] { 1, 2,3,3,2 });
                    table1.setWidthPercentage(100);
                    table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    table1.addCell(new PdfPCell(new Paragraph("№",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Помещение",font)));
                    table1.addCell(new PdfPCell(new Paragraph("№ этажа",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Температура",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Примечание",font)));


                    table1.addCell(new PdfPCell(new Paragraph("1",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Кухня",font)));
                    table1.addCell(new PdfPCell(new Paragraph(jsonHelper.getValue(ZAMER_LVL),font)));
                    table1.addCell(new PdfPCell(new Paragraph(jsonHelper.getValue(ZAMER_K_T),font)));
                    table1.addCell(new PdfPCell(new Paragraph(jsonHelper.getValue(ZAMER_K_P),font)));


                    table1.addCell(new PdfPCell(new Paragraph("2",font)));
                    table1.addCell(new PdfPCell(new Paragraph("Сан.узел",font)));
                    table1.addCell(new PdfPCell(new Paragraph(jsonHelper.getValue(ZAMER_LVL),font)));
                    table1.addCell(new PdfPCell(new Paragraph(jsonHelper.getValue(ZAMER_S_T),font)));
                    table1.addCell(new PdfPCell(new Paragraph(jsonHelper.getValue(ZAMER_S_P),font)));

                    document.add(table1);


                    document.add(new Paragraph("Замеры проводились термометром: "+jsonHelper.getValue(ZAMER_TER)+", время слива воды для замера — 3 минуты.",font));

                    document.add(new Paragraph("\n " ));



                    document.add(table_signature);
                    document.newPage();
                }

                if (photoList.size()>0){
                    document.newPage();
                    for (int i=0;i<photoList.size();i++){
                        Bitmap bitmap =decodeFile(photoList.get(i),document);
                        if (bitmap!=null){
                            int j=i+1;

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            Image image_att = Image.getInstance(stream.toByteArray());
                            document.add(new Paragraph("Приложение № "+j,font));
                            document.add(image_att);
                        }
                    }

                }

                document.close();

            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
                document.newPage();
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
                document.newPage();
                document.close();
            } catch (JSONException e) {
                e.printStackTrace();
                document.newPage();
                document.close();
            }

            return pdfFile.toString();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            pdfView.fromFile(new File(s)).load();
        }
    }
    Paragraph application(int i){
        Paragraph PRILOGE1 = new Paragraph("Приложение №"+i+" \n" +
                "к соглашению о передаче информации\n" +
                "о вводе в эксплуатацию\n" +
                "индивидуальных приборов учета\n" +
                "от 01 марта 2016г.\n \n \n", font8);

        PRILOGE1.setAlignment(Element.ALIGN_RIGHT);
        return PRILOGE1;

    }
    private Bitmap decodeFile(String imgPath, Document document){
        Bitmap b = null;
        int max_size = Math.round(document.getPageSize().getWidth());
        File f = new File(imgPath);
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            int scale = 1;
            if (o.outHeight > max_size || o.outWidth > max_size)
            {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(max_size / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        }
        catch (Exception e)
        {
        }
        return b;
    }

}
