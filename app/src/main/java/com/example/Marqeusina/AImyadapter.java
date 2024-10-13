package com.example.Marqeusina;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AImyadapter extends RecyclerView.Adapter<AImyadapter.myviewholder>
{
    public ArrayList<AImodel> datalist;
   // public Context mContext;


  //  public PyObject pyobj;
 //   public PyObject obj;


    //public myadapter(Context mContext, ArrayList<model> datalist) {
    //this.mContext = mContext;
    //this.datalist = datalist;
    // }

    public AImyadapter(ArrayList<AImodel> datalist)
    {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.aisinglerow,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, @SuppressLint("RecyclerView") int position) {
        holder.t1.setText(datalist.get(position).getMusername());
        holder.t2.setText(datalist.get(position).getMusermail());
        holder.t4.setText(datalist.get(position).getRatings());

        //SET STATS OF NLP MODLE {OPINION MINING of USER COMMENTS}

       // Toast.makeText(mContext, "---->"+nlp_stats, Toast.LENGTH_SHORT).show();

      //  String reviews_stats_list[] = nlp_stats.split(",");

        //holder.t6.setText(pos); //pos
        //holder.t8.setText(neu); //neu
        //holder.t10.setText(neg); //neg

        //

        //set image
        Glide.with(holder.marquee_profile.getContext()).load(datalist.get(position).getMpurl())
                .into(holder.marquee_profile);

        holder.view_details.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               // obj.close();
                //pyobj.close();

                MarqueeDetails ldf = new MarqueeDetails ();
                Bundle args = new Bundle();
                args.putString("marquee name", datalist.get(position).getMusername());
                args.putString("marquee email", datalist.get(position).getMusermail());
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
        TextView t1,t2,view_details,t4; //pos, neu and neg [t6 8 10]
        CircleImageView marquee_profile;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            t1=itemView.findViewById(R.id.t1); //name
            t2=itemView.findViewById(R.id.t2); //email

            view_details=itemView.findViewById(R.id.t11); //view marquee details

            t4=itemView.findViewById(R.id.t4); //ratings
            marquee_profile = (CircleImageView)itemView.findViewById(R.id.marquee_profile); //marque profile url
        }
    }
}


