package com.example.Marqeusina;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link commentsONmarquee#newInstance} factory method to
 * create an instance of this fragment.
 */
public class commentsONmarquee extends Fragment {
    public double seekbar_rat_conversion=0;

    public TextView tv1, rat;

    //send booking email to marquee owner
    public ArrayList<String> emails;
    public String Message_subject="", subject="", message="";
    //

    //get marquee's email through shared pref.
    public SharedPreferences user_info;
    public String uEmail="";
    //
    Map<String,Object> items = new HashMap<>(); //for ratings [new]
    Map<String,Object> items2 = new HashMap<>(); //for comments
    //

    public FirebaseFirestore dbroot;

    public String marquee_name = "", marquee_email="", ratings="", mpurl="";
    public String user_name = "";

    //seek bars
    private SeekBar seekbar1;
    //
    public Button post_comment;

    public double new_rat = 0.0, conv_rat;
    public String Mratings="";

    // Spinner element
    public Spinner ratingsSpinner;
    List<String> categoriesMname;

    public EditText ed_comments;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public commentsONmarquee() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment settingfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static commentsONmarquee newInstance(String param1, String param2) {
        commentsONmarquee fragment = new commentsONmarquee();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments_o_nmarquee, container, false);

        //show rate error
        tv1 = view.findViewById(R.id.tv1);
        rat = view.findViewById(R.id.rat);

        //send mail to marquee after booking of a user
        emails = new ArrayList<String>();
        //

        dbroot=FirebaseFirestore.getInstance();
        //--->Store shared pref.
        user_info=(SharedPreferences)this.getActivity().getSharedPreferences("user_data", MODE_PRIVATE);

        if(!user_info.getString("user_email","").isEmpty())
        {
            uEmail =  user_info.getString("user_email","");
        }

        //COLLECTING USER INFO. HERE FOR COMMENTS PURPOSE [Note: we have user name from shared pref.]
        //user_name = "", user_email="", user_contact="";
        dbroot.collection("MarqueeFinders")
                .whereEqualTo("email", uEmail) //static username but will use shared pref here
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                user_name = document.getString("name");
                                //user_email = document.getString("email");
                                //user_purl = document.getString("purl");
                                items2.put("username",document.getString("name"));
                                items2.put("user_email",document.getString("email"));
                                items2.put("purl",document.getString("purl"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        //

        ed_comments = (EditText)view.findViewById(R.id.ed_comments);
        //seekbar
        seekbar1 = (SeekBar) view.findViewById(R.id.slider1);// marquee names
        post_comment = (Button)view.findViewById(R.id.post_comment);//user comment

        //---------------------------------------------Marquees Name spinner---------------------------------------------------//

        // Spinner element
        ratingsSpinner = (Spinner) view.findViewById(R.id.packages);

        // Spinner Drop down elements POPULATE package adapter
        categoriesMname = new ArrayList<String>();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterP = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categoriesMname);

        // Drop down layout style - list view with radio button
        dataAdapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ratingsSpinner.setAdapter(dataAdapterP);

        //Qeury
        dbroot.collection("MarqueeOwners")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                subject = document.getString("name");
                                categoriesMname.add(subject);

                                Mratings = document.getString("ratings");
                                conv_rat = Double.parseDouble(Mratings);

                                //marquee email
                               // marquee_email = document.getString("email");
                            }
                            dataAdapterP.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        ratingsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // On selecting a spinner item
                marquee_name = adapterView.getItemAtPosition(i).toString();

                //Qeury
                dbroot.collection("MarqueeOwners")
                        .whereEqualTo("name",marquee_name)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());

                                        //marquee email
                                        marquee_email = document.getString("email");
                                        mpurl = document.getString("purl");
                                    }
                                    dataAdapterP.notifyDataSetChanged();
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        seekbar1.incrementProgressBy(1);
        seekbar1.setProgress(0); //starting value
        seekbar1.setMax(10); //ending value 10/2 = 5 [ratings max == 5] ok
        //set sb1 listener///////////////////////////////////////////////////////////
        seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress1,
                                          boolean fromUser) {
                // TODO Auto-generated method stub

                ratings = String.valueOf((double) progress1 / 2);
                rat.setText(String.valueOf((double) progress1 / 2));
               // Toast.makeText(getContext(), "rated: "+ratings, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
        /////////////////////////////sb1 end/////////////////////////////////

        //post comment button listener
        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ed_comments.getText().toString().trim().isEmpty())
                {
                    ed_comments.setError("Please provide your comment here!");
                    ed_comments.requestFocus();
                    return;
                }
                else
                {
                    ed_comments.setError(null);
                    ed_comments.clearFocus();
                }

                if(ratings.equals("0"))
                {
                    tv1.setError("Please provide your ratings i.e., below comments!");
                    tv1.requestFocus();
                    return;
                }
                else
                {
                    tv1.setError(null);
                    tv1.clearFocus();
                }

                //we will get user date here from shared pref.
                //ratings update
                if(!ratings.isEmpty())
                {
                    seekbar_rat_conversion = Double.parseDouble(ratings);
                    tv1.setError(null);
                    tv1.clearFocus();
                }
                else
                {
                    tv1.setError("Please provide your ratings i.e., below comments!");
                    tv1.requestFocus();
                    return;
                }
                new_rat = ((conv_rat + seekbar_rat_conversion) / 2);
                String formatted_Rat_Value = String.format("%.1f", new_rat);//rounding of to one decimal place
                //Toast.makeText(getContext(), "new marquee ratings: " + formatted_Rat_Value, Toast.LENGTH_SHORT).show();

                //ratings hashmap
                items.put("ratings", String.valueOf(formatted_Rat_Value)); //put inside marquee table
                //Toast.makeText(getContext(), "marquee_email: "+marquee_email, Toast.LENGTH_SHORT).show();
                dbroot.collection("MarqueeOwners").document(marquee_email).set(items, SetOptions.merge());

                //comments hashmap [also need user info.]
                items2.put("comment", ed_comments.getText().toString().trim());
                // items2.put("username",user_name);
                //items2.put("usermail",user_email);
                // items2.put("purl",user_purl);
                items2.put("musername", marquee_name);
                items2.put("musermail", marquee_email);
                items2.put("ratings", ratings);
                items2.put("mpurl",mpurl);
                //Toast.makeText(getContext(), "You chose to rate " + ratings + " to the " + "marquee", Toast.LENGTH_SHORT).show();

                final int[] c = {0};
                //check user status qeury-------------------------------------------------------------
                dbroot.collection("Bookings").document(marquee_email)
                        .collection("booked_users")
                        .whereEqualTo("email", uEmail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    Toasty.error(getContext(), "c[0]: "+c[0], Toast.LENGTH_SHORT).show();
                                    //if(c[0] < 1)
                                    //{
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            if (document.exists() && c[0] < 1)
                                            {
                                                //marquee email
                                                Toasty.success(getContext(), "c[0]: "+c[0], Toast.LENGTH_SHORT).show();
                                                if (document.getString("status").equals("visited")) {
                                                    //---------------------------------------SEND MAIL----------------------------------------//
                                                    emails.add(marquee_email);

                                                    if (!emails.isEmpty())
                                                    {
                                                        //set subject of message
                                                        //Toast.makeText(getContext(), "username: " + user_name, Toast.LENGTH_SHORT).show();
                                                        Message_subject = "Congrats! New Feedback Arrival here";
                                                        //set  message
                                                        StringBuilder message = new StringBuilder();

                                                        message.append("User " + user_name + " has provided their feedback on your marquee which includes a comment as '" +
                                                                ed_comments.getText().toString().trim() + "' and provided ["+ratings+"] ratings to your marquee'");

                                                        String finalString = message.toString();

                                                        String mSubject = Message_subject;
                                                        String mMessage = message.toString().trim();


                                                        JavaMailAPIs javaMailAPIs = new JavaMailAPIs(getContext(), emails, mSubject, mMessage);

                                                        javaMailAPIs.execute();
                                                        Toasty.success(getContext(), "Notification Sent Successfully", Toast.LENGTH_SHORT).show();
                                                        c[0] +=1;
                                                    }
                                                    else
                                                    {
                                                        Toasty.error(getContext(), "Error occurred! While sending Notification", Toast.LENGTH_SHORT).show();
                                                    }
                                                    //----------------------------------------------------------------------------------------//

                                                    dbroot.collection("Comments").document(marquee_email)
                                                            .collection("user_comments").add(items2);//comment added
                                                }
                                                else if(document.getString("status").equals("booked"))
                                                {
                                                    Toasty.info(getContext(), "You must be a {VISITED USER of the " + marquee_name + "} to provide your feedback", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                   // }

                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(getContext(), "Error Occurred! Check Your Connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                //------------------------------------------------------------------------------------
            }
        });

        return  view;
    }
}