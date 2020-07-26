package ru.ekbapp.intexartwoker.Module;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH; // полный путь к базе данных
    private static String DB_NAME = "data17.db";
    private static final int SCHEMA = 1; // версия базы данных
    // названия столбцов
    static final String TABLE_ADRES = "adresa";
    static final String TABLE = "data";
    static final String TABLE_DOCUMENT = "document_list";
    static final String TABLE_REQUST = "request";
    static final String TABLE_WORK_LIST = "work_list";
    static final String TABLE_FILDS = "filds";
    static final String TABLE_CLIENT = "client";
    static final String TABLE_VOISE = "voise_jornal";


    static final String COLUMN_ID = "_id";

    static final String COLUMN_A_ADRES = "adres";

    static final String COLUMN_D_REQUEST = "request";
    static final String COLUMN_D_LINK = "link";
    static final String COLUMN_D_STATUS = "status";
    static final String COLUMN_D_TEXT = "text";
    static final String COLUMN_D_NAME = "name";
    static final String COLUMN_D_PACKAGE  = "user_s";
    static final String COLUMN_D_COST  = "cost";

    static final String COLUMN_C_REQUEST  = "request_id";
    static final String COLUMN_C_LINK  = "link";
    static final String COLUMN_C_STATUS  = "status";


    static final String COLUMN_F_STATUS  = "status";
    static final String COLUMN_F_VALUE  = "value";
    static final String COLUMN_F_COD  = "cod";
    static final String COLUMN_F_PACKAGE  = "package";
    static final String COLUMN_F_REQUEST_ID = "request_id";

    static final String COLUMN_R_ID_REQUEST = "id_req";
    static final String COLUMN_R_STATUS = "status";
    static final String COLUMN_R_ADRES = "adress";
    static final String COLUMN_R_APATMENT = "apatment";
    static final String COLUMN_R_MAN = "man";
    static final String COLUMN_R_TYPE = "type";
    static final String COLUMN_R_COMMENT = "comment";
    static final String COLUMN_R_RES = "res";
    static final String COLUMN_R_EXECUTOR = "executor";
    static final String COLUMN_R_DATA_END = "date_end";
    static final String COLUMN_R_KONTACT = "contakt";
    static final String COLUMN_R_DOCUMENT = "document";
    static final String COLUMN_R_TYPE_TWO = "type_two";
    static final String COLUMN_R_NOMER = "nomer";
    static final String COLUMN_R_YK = "yk";
    static final String COLUMN_R_DATECREATE = "datecreate";

    static final String COLUMN_W_NAME_WORK = "name";
    static final String COLUMN_W_CODE_WORK = "code";
    static final String COLUMN_W_UNITS_WORK = "units";
    static final String COLUMN_W_COSTPERUNIT_WORK = "costRerUnit";

    static final String COLUMN_F_NAME = "name";
    static final String COLUMN_F_VALVE = "valve";
    static final String COLUMN_F_NOMER = "nomer";
    static final String COLUMN_F_DOCUMENT = "document";

    static final String COLUMN_V_REQUEST = "request";
    static final String COLUMN_V_LINK = "link";
    static final String COLUMN_V_STATUS = "status";


    static final String COLUMN_REQUESQ = "reqeust";
    static final String COLUMN_FILE = "file_pass";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_TEXT1 = "text1";
    static final String COLUMN_TEXT2 = "text2";
    static final String COLUMN_TEXT3 = "text3";

    //Таблица вложений
    static final String COLUMN_ATTACHMENT = "photo";

    static final String COLUMN_T_REQUEST_ID = "request";
    static final String COLUMN_T_STATUS = "status";
    static final String COLUMN_T_PDF = "photo";

    static final int COLUMN_T_REQUEST_id = 1;
    static final int COLUMN_T_STATUS_id = 5;
    static final int COLUMN_T_PDF_id = 2;

    public static final String COLUMN_NEW_REQ_NUMBER="number";
    public static final String COLUMN_NAME="newRequest";
    public static final String COLUMN_ID_REQ="idreq";
    public static final String COLUMN_NEW_REQ_STATUS="status";


    private Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, SCHEMA);
        this.myContext=context;
        DB_PATH =context.getFilesDir().getPath() + DB_NAME;



    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void create_db(){
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                this.getReadableDatabase();
                //получаем локальную бд как поток
                myInput = myContext.getAssets().open(DB_NAME);
                // Путь к новой бд
                String outFileName = DB_PATH;

                // Открываем пустую бд
                myOutput = new FileOutputStream(outFileName);

                // побайтово копируем данные
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
            }
        }
        catch(IOException ex){
            Log.d("DatabaseHelper", ex.getMessage());
        }
    }
    public SQLiteDatabase open()throws SQLException {

        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
