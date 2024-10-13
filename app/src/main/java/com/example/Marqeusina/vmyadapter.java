package com.example.Marqeusina;


import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class vmyadapter extends RecyclerView.Adapter<vmyadapter.myviewholder>
{

    //send booking email to marquee owner
    public ArrayList<String> emails;
    public String subject="", message="";
    //

    //
    Map<String,Object> items = new HashMap<>();
    //
    public String docID = "";
    FirebaseFirestore dbroot;
    //get marquee's email through shared pref.
    public SharedPreferences marquee_info;
    public String mEmail="", mName="";

    ArrayList<vmodel> datalist;
    public Context mContext;


    //public vmyadapter(ArrayList<vmodel> datalist) {
      //  this.datalist = datalist;
   // }

    public vmyadapter(Context mContext, ArrayList<vmodel> datalist) {
    this.mContext = mContext;
    this.datalist = datalist;
     }

    //public vmyadapter(ArrayList<vmodel> datalist) {
        //this.datalist = datalist;
    //}

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.vsinglerow,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, @SuppressLint("RecyclerView") int position) {
        //send mail to marquee after booking of a user
        emails = new ArrayList<String>();

        dbroot = FirebaseFirestore.getInstance();
        //--->Store shared pref.

        marquee_info = mContext.getSharedPreferences("marquee_data", MODE_PRIVATE);
        mEmail =  marquee_info.getString("marquee_email","");
        if(!mEmail.isEmpty())
        {

            //Toast.makeText(mContext.getApplicationContext(), "->marquee_email is: "+mEmail, Toast.LENGTH_SHORT).show();
            dbroot.collection("MarqueeOwners")
                    .whereEqualTo("email",mEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list)
                            {
                                if (d.getString("name") != null)
                                {
                                    mName = d.getString("name"); //get marquee name
                                }
                            }

                        }
                    });
        }

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

        holder.mark_visited.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                items.put("status","visited");
                dbroot.collection("Bookings").document(mEmail).collection("booked_users")
                        .whereEqualTo("Date",datalist.get(position).getDate())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list)
                                {
                                    if (d.getString("name") != null)
                                    {
                                        docID = d.getId();
                                        //Toast.makeText(mContext.getApplicationContext(), "->docID is: "+docID, Toast.LENGTH_SHORT).show();
                                        dbroot.collection("Bookings").document(mEmail).collection("booked_users")
                                                .document(docID).set(items, SetOptions.merge());
                                    }
                                }

                            }
                        });

                //send mail also tu the user for asking their comment
                String user_email = datalist.get(position).getEmail();
                if (!user_email.isEmpty())
                    emails.add(user_email);
                subject = "Hurry Up !!! Give Your Feedback"; //set subject
                if(!mName.isEmpty())
                    message = "Please provide your precious feedback and ratings on marquee i.e., ["+mName+"] \nas you have utilized their services physically\n because it will help other customers in choosing best marquees.\nThanks";
                else
                {
                    message ="Please provide your precious feedback and ratings on marquee \nas you have utilized their services physically\n because it will help other customers in choosing best marquees.\nThanks";
                }
                if(!emails.isEmpty())
                {
                    JavaMailAPIs javaMailAPIs = new JavaMailAPIs(mContext, emails, subject, message);
                    Toasty.success(mContext.getApplicationContext(), "Notified users successfully", Toast.LENGTH_SHORT).show();
                    javaMailAPIs.execute();
                }
                else
                {
                    Toasty.error(mContext.getApplicationContext(), "Notified users UN-Successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        TextView user_name,no_of_guests_coming,pkg_name,event_date, t7, t9, t11, t13;
        CircleImageView user_profile;
        Button mark_visited;
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

            mark_visited = itemView.findViewById(R.id.mark_visited);
        }
    }
}



