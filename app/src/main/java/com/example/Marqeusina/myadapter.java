package com.example.Marqeusina;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class myadapter extends RecyclerView.Adapter<myadapter.myviewholder>
{
    ArrayList<model> datalist;
    //private Context mContext;
    //
    Map<String,Object> items = new HashMap<>();
    //


    //public myadapter(Context mContext, ArrayList<model> datalist) {
        //this.mContext = mContext;
        //this.datalist = datalist;
   // }

    public myadapter(ArrayList<model> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, @SuppressLint("RecyclerView") int position) {
        holder.t1.setText(datalist.get(position).getName());
        holder.t2.setText(datalist.get(position).getEmail());
        holder.t4.setText(datalist.get(position).getRatings());

        //set image
        Glide.with(holder.marquee_profile.getContext()).load(datalist.get(position).getPurl())
        .into(holder.marquee_profile);

        holder.view_details.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MarqueeDetails ldf = new MarqueeDetails ();
                Bundle args = new Bundle();
                args.putString("marquee name", datalist.get(position).getName());
                args.putString("marquee email", datalist.get(position).getEmail());
                ldf.setArguments(args);

                AppCompatActivity getActivity = (AppCompatActivity) view.getContext();
                getActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ldf, "findThisAdapterOnClickFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        TextView t1,t2,view_details,t4;
        CircleImageView marquee_profile;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            t1=itemView.findViewById(R.id.t1); //name
            t2=itemView.findViewById(R.id.t2); //email

            view_details=itemView.findViewById(R.id.t5); //view marquee details

            t4=itemView.findViewById(R.id.t4); //ratings
            marquee_profile = (CircleImageView)itemView.findViewById(R.id.marquee_profile); //marque profile url
        }
    }
}

