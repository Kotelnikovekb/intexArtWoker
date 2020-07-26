package ru.ekbapp.intexartwoker.Class;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import ru.ekbapp.intexartwoker.Module.Share_void;

public class JsonHelper {
    public final File file;

    static String TAG_S = "draft_module";

    public static final String SIGNATURE_ARRAY = "Signature";
    public static final String PATCH_SIGNATURE = "patch";
    public static final String STATUS_CLOSE = "close";
    public static final String PACKAGE_S = "package";

    public static final String MESSAGE_REQUEST = "message";
    public static final String DATE_CREATE_REQUEST = "dateCreate";
    public static final String CUSTOMER_NAME = "customerName";
    public static final String CITY = "city";
    public static final String STREET = "street";
    public static final String APARTMENT = "apartment";
    public static final String BASE = "base";
    public static final String WORKS_ARRAY = "works";
    public static final String PHOTO_ARRAY = "photo";
    public static final String COUNTER_ARRAY = "counter";
    public static final String CUSTOMER_PHONE = "phone";
    public static final String EXECUTOR_ROLE = "EXECUTOR_ROLE";


    public static final String ADDRESS_DOGOVOR_NUMBER = "ADDRESS_DOGOVOR_NUMBER";
    public static final String ADDRESS_DOGOVOR_DATE = "ADDRESS_DOGOVOR_DATE";
    public static final String COMPANY_NAME = "COMPANY_NAME";


    public static final String WORKS_NAME = "name";
    public static final String WORKS_COUNT = "count";
    public static final String WORKS_ED = "ed";
    public static final String WORKS_PRICE = "price";
    public static final String WORKS_COST = "cost";

    public static final String COUNTER_POSITION = "Position";
    public static final String COUNTER_TYPE = "Type";
    public static final String COUNTER_NUMBER = "Number";
    public static final String COUNTER_DATE = "DateNextCheck";
    public static final String COUNTER_PEAL = "Peal";
    public static final String COUNTER_INDICATION = "Indications";

    public static final String EXECUTOR_NAME_GENITIVE = "full_name__genitive";
    public static final String EXECUTOR_NAME_DATIVE = "full_name_dative";
    public static final String EXECUTOR_NAME_ABLATIVE = "full_name_ablative";

    //акт обследования
    public static final String OBJECT_SEE_ACT = "objectSee";
    public static final String OBJECT_SET_ACT = "objectSet";
    public static final String OBJECT_TOTAL_ACT = "objectTotal";
    //Акт
    //замера температуры ГВС
    public static final String ZAMER_T1 = "gvs_t_vt1_s";
    public static final String ZAMER_T2 = "gvs_t_vt2_s";
    public static final String ZAMER_T3 = "gvs_t_vt3_s";
    public static final String ZAMER_T4 = "gvs_t_vt4_s";

    public static final String ZAMER_P1 = "gvs_t_ba1_s";
    public static final String ZAMER_P2 = "gvs_t_ba2_s";

    public static final String ZAMER_TER = "gvs_t_ter_s";

    public static final String ZAMER_LVL = "gvs_t_vlv_s";
    public static final String ZAMER_K_T = "gvs_t_tkx_s";
    public static final String ZAMER_K_P = "gvs_t_pkx_s";
    public static final String ZAMER_S_T = "gvs_t_tcy_s";
    public static final String ZAMER_S_P = "gvs_t_pcy_s";







    public JsonHelper(File file) {
        this.file = file;
    }

    public  boolean createJson( JSONObject result){
        boolean answer=false;
        try {
            JSONObject title = new JSONObject();
            title.put("id", result.getString("id"));
            title.put("number", "");
            title.put("street",  result.getString("street"));
            title.put("apartment",  result.getString("apartment"));
            title.put("dateCreate", Share_void.parseDateToddMMyyyyHHmm(result.getString("dateCreate")));
            title.put("message", result.getString("message"));
            title.put("executor", result.getString("executor"));
            title.put("customerName", result.getString("customerText"));
            title.put("base", result.getString("base"));
            title.put("city", result.getString("city"));
            title.put(ADDRESS_DOGOVOR_NUMBER,result.getString("numberDogovor"));
            title.put(ADDRESS_DOGOVOR_DATE,result.getString("dateDogovor"));
            title.put(COMPANY_NAME,result.getString("companyName"));
            title.put(CUSTOMER_PHONE,result.getString("customerPhone"));
            title.put(EXECUTOR_ROLE,result.getString("executorRole"));
            title.put(EXECUTOR_NAME_GENITIVE,result.getString("name_genitive"));
            title.put(EXECUTOR_NAME_DATIVE,result.getString("name_dative"));
            title.put(EXECUTOR_NAME_ABLATIVE,result.getString("name_ablative"));


            /*
            if (file.exists()){
                JSONArray works=getArray(WORKS_ARRAY);
                if (works!=null){
                    title.put(WORKS_ARRAY,works);
                }
                JSONArray photo=getArray(PHOTO_ARRAY);
                if (photo!=null){
                    title.put(PHOTO_ARRAY,photo);
                }
                JSONArray counter=getArray(COUNTER_ARRAY);
                if (counter!=null){
                    title.put(COUNTER_ARRAY,counter);
                }
            }

             */

            Share_void.writeFile(title,file);
        }catch (JSONException e){
            e.printStackTrace();
            Log.e(TAG_S,e.toString());
        }
        return answer;

    }
    public String getValue(String value){
        String answer="null";
        try {
            if (file.exists()){
                try{
                    String json = Share_void.readFile(file);
                    JSONObject object = new JSONObject(json);
                    if (object.has(value)) {
                        answer = object.getString(value);
                    }
                    Log.e(TAG_S, "Данные о " + value + " получены ");
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e(TAG_S, "Ошибка получения " + value + "\n" + e.toString());
                }
            }
        }catch (Exception ex){

        }




        return answer;
    }
    public JSONArray getArray(String name){
        JSONArray answer=new JSONArray();

        if (file.exists()){
            try{
                String json = Share_void.readFile(file);

                JSONObject object = new JSONObject(json);
                if (object.has(name)) {
                    answer = object.getJSONArray(name);
                }
                Log.e(TAG_S, "Данные о " + name + " получены ");
            }catch (JSONException e){
                e.printStackTrace();
                Log.e(TAG_S, "Ошибка получения " + name + "\n" + e.toString());
            }
        }

        return answer;

    }
    public JSONArray getArray(JSONObject object,String name){
        JSONArray answer=null;

        if (file.exists()){
            try{
                if (object.has(name)) {
                    answer = object.getJSONArray(name);
                }
                Log.e(TAG_S, "Данные о " + name + " получены ");
            }catch (JSONException e){
                e.printStackTrace();
                Log.e(TAG_S, "Ошибка получения " + name + "\n" + e.toString());
            }
        }

        return answer;

    }
    public boolean addPhoto(String patch){
        boolean answer=false;
        String json = Share_void.readFile(file);
        try{
            JSONObject object = new JSONObject(json);
            JSONArray photoArray=getArray(object,PHOTO_ARRAY);
            if (photoArray!=null){
                photoArray.put(patch);
                object.remove(PHOTO_ARRAY);
                object.put(PHOTO_ARRAY,photoArray);
            }else{
                object.put(PHOTO_ARRAY,new JSONArray().put(patch));
            }
            Share_void.writeFile(object,file);
            answer=true;
        }catch (JSONException e){
            e.printStackTrace();
            Log.e(TAG_S,e.toString());
        }

        return answer;
    }
    public boolean addWork(WorkItem workItem){
        boolean answer=false;
        String json = Share_void.readFile(file);
        try{
            JSONObject object = new JSONObject(json);
            JSONArray photoArray=getArray(object,WORKS_ARRAY);
            JSONObject work=new JSONObject();
            work.put(WORKS_NAME,workItem.getName());
            work.put(WORKS_COST,workItem.getCost());
            work.put(WORKS_COUNT,workItem.getCount());
            work.put(WORKS_ED,workItem.getEd());
            work.put(WORKS_PRICE,workItem.getPrice());
            if (photoArray!=null){
                photoArray.put(work);
                object.remove(WORKS_ARRAY);
                object.put(WORKS_ARRAY,photoArray);
            }else{
                object.put(WORKS_ARRAY,new JSONArray().put(work));
            }
            Share_void.writeFile(object,file);
            answer=true;
        }catch (JSONException e){
            e.printStackTrace();
            Log.e(TAG_S,e.toString());
        }

        return answer;
    }
    public boolean addCounter(CounterClass counterItem){
        boolean answer=false;
        String json = Share_void.readFile(file);
        try{
            JSONObject object = new JSONObject(json);
            JSONArray photoArray=getArray(object,COUNTER_ARRAY);
            JSONObject work=new JSONObject();
            work.put(COUNTER_TYPE,counterItem.getType());
            work.put(COUNTER_DATE,counterItem.getDateNextCheck());
            work.put(COUNTER_INDICATION,counterItem.getIndications());
            work.put(COUNTER_NUMBER,counterItem.getNumber());
            work.put(COUNTER_PEAL,counterItem.getPeal());
            work.put(COUNTER_POSITION,counterItem.getPosition());
            if (photoArray!=null){
                photoArray.put(work);
                object.remove(COUNTER_ARRAY);
                object.put(COUNTER_ARRAY,photoArray);
            }else{
                object.put(COUNTER_ARRAY,new JSONArray().put(work));
            }
            Share_void.writeFile(object,file);
            answer=true;
        }catch (JSONException e){
            e.printStackTrace();
            Log.e(TAG_S,e.toString());
        }

        return answer;
    }
    public void setPackage(String id){
        if (file.exists()){
            String json = Share_void.readFile(file);
            try {
                JSONObject jsonObject=new JSONObject(json);
                if (jsonObject.has(PACKAGE_S)){
                    jsonObject.remove(PACKAGE_S);
                    jsonObject.put(PACKAGE_S,id);
                }else {
                    jsonObject.put(PACKAGE_S,id);
                }
                Share_void.writeFile(jsonObject,file);
            }catch (JSONException e){
                e.printStackTrace();
                Log.e("JSON_HELPER","Ошибка добавления пакета: \n"+e.getMessage());
            }
        }

    }
    public void setParametr(String parametr, String value){
        if (file.exists()){
            String json = Share_void.readFile(file);
            try {
                JSONObject jsonObject=new JSONObject(json);
                if (jsonObject.has(parametr)){
                    jsonObject.remove(parametr);
                    jsonObject.put(parametr,value);
                }else {
                    jsonObject.put(parametr,value);
                }
                Share_void.writeFile(jsonObject,file);
            }catch (JSONException e){
                e.printStackTrace();
                Log.e("JSON_HELPER","Ошибка добавления "+parametr+" : \n"+e.getMessage());
            }
        }

    }
}
