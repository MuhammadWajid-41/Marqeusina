package com.example.Marqeusina;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class bmyadapter extends RecyclerView.Adapter<bmyadapter.myviewholder>
{
    ArrayList<bmodel> datalist;
    //private Context mContext;


    //public myadapter(Context mContext, ArrayList<model> datalist) {
    //this.mContext = mContext;
    //this.datalist = datalist;
    // }

    public bmyadapter(ArrayList<bmodel> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.bsinglerow,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, @SuppressLint("RecyclerView") int position) {
        holder.user_name.setText(datalist.get(position).getName());
        holder.no_of_guests_coming.setText(datalist.get(position).getGuests_coming());
        holder.pkg_name.setText(datalist.get(position).getPackage_name());
        holder.event_date.setText(datalist.get(position).getDate());

        holder.t7.setText(datalist.get(position).getEvent_time());
        holder.t9.setText(datalist.get(position).getEvent_type());
        holder.t11.setText(datalist.get(position).getPackage_price());
        holder.t13.setText(datalist.get(position).getToken_payed());

        //set image
        if (!datalist.get(position).getPurl().isEmpty())
        {
            Glide.with(holder.user_profile.getContext()).load(datalist.get(position).getPurl())
                    .into(holder.user_profile);
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        TextView user_name,no_of_guests_coming,pkg_name,event_date, t7, t9, t11, t13;

        CircleImageView user_profile;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            user_name=itemView.findViewById(R.id.user_name);
            no_of_guests_coming=itemView.findViewById(R.id.no_of_guests_coming);
            pkg_name=itemView.findViewById(R.id.pkg_name);
            event_date=itemView.findViewById(R.id.event_date);

            t7=itemView.findViewById(R.id.t7);
            t9=itemView.findViewById(R.id.t9);
            t11=itemView.findViewById(R.id.t11);
            t13=itemView.findViewById(R.id.t13);

            user_profile = (CircleImageView)itemView.findViewById(R.id.user_profile); //user profile url
        }
    }
}



