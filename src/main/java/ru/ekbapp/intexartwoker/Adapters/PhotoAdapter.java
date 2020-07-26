package ru.ekbapp.intexartwoker.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


import ru.ekbapp.intexartwoker.Class.PhotoClass;
import ru.ekbapp.intexartwoker.R;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    Context context;
    List<PhotoClass> list;
    private LayoutInflater mInflater;

    public PhotoAdapter(Context context, List<PhotoClass> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(list.get(position).getBitmap()!=null){
            Log.e("linkDowload","dowload susses");
            holder.progressBar.setVisibility(View.GONE);
            holder.imageView.setImageBitmap(list.get(position).getBitmap());
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }
    public void addItem(String URL){
        PhotoClass photoClass=new PhotoClass(URL,null);
        list.add(photoClass);
        notifyDataSetChanged();
    }

    public List<String> getListPatch(){
        List<String> listAnswer=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            listAnswer.add(list.get(i).getPatch());
        }
        return listAnswer;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView myCardView;
        RelativeLayout myRelativeLayout;
        ProgressBar progressBar;
        FloatingActionButton close;
        ImageView imageView;

        ViewHolder(final View itemView) {
            super(itemView);
            close=itemView.findViewById(R.id.floatingActionButton2);
            myCardView=itemView.findViewById(R.id.card);
            progressBar=itemView.findViewById(R.id.progressBar7);
            imageView=itemView.findViewById(R.id.imageView2);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(getAdapterPosition());
                    notifyDataSetChanged();

                }
            });

          //  close.setVisibility(View.GONE);
            myCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*
                    Log.e("linkDowload",getAdapterPosition()+"");
                    Intent intent = new Intent(context, ImageListActivity.class);
                    ArrayList<PhotoClass> l =new ArrayList<PhotoClass>(list);;
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST",(Serializable)l);
                    intent.putExtra("BUNDLE",args);
                    intent.putExtra("number",getAdapterPosition());
                    context.startActivity(intent);

                     */

                }
            });

            /*
            myTitle = itemView.findViewById(R.id.textView_title);
            mySubTitle = itemView.findViewById(R.id.textView_subtitle);
            myCardView=itemView.findViewById(R.id.card_ads);
            myRelativeLayout=itemView.findViewById(R.id.RelativeCard);
            itemView.setOnClickListener(this);

             */
        }



    }
}
