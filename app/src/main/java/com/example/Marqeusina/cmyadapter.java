package com.example.Marqeusina;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class cmyadapter extends RecyclerView.Adapter<cmyadapter.myviewholder>
{
    ArrayList<cmodel> cdatalist;

    public cmyadapter(ArrayList<cmodel> cdatalist) {
        this.cdatalist = cdatalist;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.csinglerow,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.t1.setText(cdatalist.get(position).getUsername());
        holder.t2.setText(cdatalist.get(position).getComment());
        holder.t3.setText(cdatalist.get(position).getRatings());

        //set image
        Glide.with(holder.user_profile.getContext()).load(cdatalist.get(position).getPurl())
                .into(holder.user_profile);
    }

    @Override
    public int getItemCount() {
        return cdatalist.size();
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        TextView t1,t2, t3;
        CircleImageView user_profile;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            t1=itemView.findViewById(R.id.t1); //username
            t2=itemView.findViewById(R.id.t2); //comment
            t3=itemView.findViewById(R.id.t3); //ratings

            user_profile = (CircleImageView)itemView.findViewById(R.id.user_profile); //marque profile url
        }
    }
}

