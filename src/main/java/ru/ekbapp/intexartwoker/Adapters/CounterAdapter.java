package ru.ekbapp.intexartwoker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.ekbapp.intexartwoker.Class.CounterClass;
import ru.ekbapp.intexartwoker.Class.WorkItem;
import ru.ekbapp.intexartwoker.R;

public class CounterAdapter extends ArrayAdapter<CounterClass> {
    List<CounterClass> list=new ArrayList<>();
    Context Context;
    public CounterAdapter(Context context,List<CounterClass> ListInput) {
        super(context, R.layout.item_work, ListInput);
        list.addAll(ListInput);
        Context=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CounterClass counter = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_counter, null);
        }
        TextView name=convertView.findViewById(R.id.textView34);
        name.setText(counter.Type);
        switch (counter.Type){
            case "Холодная":
                name.setTextColor(Context.getResources().getColor(R.color.colorPrimary));
                break;
            case "Горячая":
                name.setTextColor(Context.getResources().getColor(R.color.error));
                break;
        }
        ((TextView)convertView.findViewById(R.id.textView35)).setText("Помещение: "+counter.Position+"");
        String data="";
        if(!counter.Number.equals("")){
            data+="\nНомер: "+counter.Number;
        }
        if(!counter.DateNextCheck.equals("")){
            data+="\nДата следующей проверки: "+counter.DateNextCheck;
        }
        if(!counter.Peal.equals("")){
            data+="\n№ пломбы: "+counter.Peal;
        }
        if(!counter.Indications.equals("")){
            data+="\nПоказания: "+counter.Indications;
        }

        ((TextView)convertView.findViewById(R.id.textView36)).setText(data);
        return convertView;
    }

}
