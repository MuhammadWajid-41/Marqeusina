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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link bill_generation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class bill_generation extends Fragment {
    public int book_marquee_button_checker=0;

    //get marquee's email through shared pref.
    public SharedPreferences user_info;
    public String uEmail="", UserName="", UserContact="", UserPurl="";

    FirebaseFirestore dbroot;
    //params
    public String event_type_of_user = "", pkgs="", progress1_to_str="0", date="", lunch_dinner="", noOfGuests="", marquee_OnClick_sent_email="", marquee_OnClick_sent_name="", pkgPrice=""; //all params to pass
    public double hall_price_double_value=0.0;
    //

    public TextView marquee_name, pkg_name, pkg_price, date_of_event, time_of_event, category_of_event, guests_comming, hall_Price, bill_generated, token;
    public Button btn1;

    public double bill=0.0, tokken=0.0, pkg_price_converted=0.0, guests_conv=0.0;

    //send booking email to marquee owner
    public ArrayList<String> emails;
    public String subject=""; //message="";
    //

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public bill_generation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment bill_generation.
     */
    // TODO: Rename and change types and number of parameters
    public static bill_generation newInstance(String param1, String param2) {
        bill_generation fragment = new bill_generation();
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
        View view = inflater.inflate(R.layout.fragment_bill_generation, container, false);

        dbroot = FirebaseFirestore.getInstance();

        /////////////////////////////////////////////////////////////////////////
        bill_generated = (TextView)view.findViewById(R.id.bill_generated); //bill
        token = (TextView)view.findViewById(R.id.token);//token
        ////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////PARAMS RECIEVED/////////////////////////////////////////////////////
        marquee_OnClick_sent_email = getArguments().getString("marquee email");
        marquee_OnClick_sent_name = getArguments().getString("marquee name");

        //-->need hall_price so need qeury here to fetch hall price

        dbroot.collection("MarqueeOwners").document(marquee_OnClick_sent_email)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if(document.get("hall_price") != null)
                        {
                            Number hall_price_value = (Number) document.get("hall_price");
                            hall_price_double_value = Double.parseDouble(String.valueOf(hall_price_value));
                            hall_Price.append("Hall Price: "+hall_price_double_value);

                            //______________________________________BILL______________________________________//
                            //-------------.NEED QUERY TO GENERATE BILL
                            //need hall price, pkgprice, number of guests [double bill=0.0, roundoff=0.0, tokken=0.0, pkg_price_converted=0.0, guests_conv]

                            pkg_price_converted = Double.parseDouble(pkgPrice);
                            guests_conv = Double.parseDouble(noOfGuests);
                            bill = 0.0;
                            bill = (hall_price_double_value + (pkg_price_converted * guests_conv));
                            bill_generated.append("Total Bill Estimates: "+bill);
                            tokken=0.0;
                            tokken = bill/10;
                            token.append("Total Bill 10% Token: "+tokken);

                            /////////////////////////////////////////////////////////////////////////
                        }

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //--->Store shared pref.
        user_info=(SharedPreferences)this.getActivity().getSharedPreferences("user_data", MODE_PRIVATE);

        if(!user_info.getString("user_email","").isEmpty())
        {
            uEmail =  user_info.getString("user_email","");
        }

        if(!uEmail.isEmpty())
        {
            dbroot.collection("MarqueeFinders").document(uEmail)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            if (document.getString("name") != null && document.getString("contact") != null && document.getString("purl") != null)
                            {
                                UserName = document.getString("name");
                                UserContact = document.getString("contact");
                                UserPurl = document.getString("purl");
                            }

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        //send mail to marquee after booking of a user
        emails = new ArrayList<String>();
        //



        event_type_of_user = getArguments().getString("event_type_of_user");
        pkgs = getArguments().getString("package");
        noOfGuests = getArguments().getString("guests coming");
        date = getArguments().getString("event_date");
        lunch_dinner = getArguments().getString("lunch_dinner");
        pkgPrice = getArguments().getString("pkgPrice");
        date = getArguments().getString("event_date");


        //TEXTVIEWS INIT
        marquee_name = (TextView)view.findViewById(R.id.marquee_name);
        pkg_name = (TextView)view.findViewById(R.id.pkg_name);
        pkg_price = (TextView)view.findViewById(R.id.pkg_price);
        date_of_event = (TextView)view.findViewById(R.id.date_of_event);
        time_of_event = (TextView)view.findViewById(R.id.time_of_event);
        category_of_event = (TextView)view.findViewById(R.id.category_of_event);
        guests_comming = (TextView)view.findViewById(R.id.guests_comming);
        hall_Price = (TextView)view.findViewById(R.id.hall_price);

        //SETTING ALL TEXT VIEWS
        marquee_name.append("Marquee Name: "+marquee_OnClick_sent_name);
        pkg_name.append("Package Name: "+pkgs);

        pkg_price.append("Package Price: "+pkgPrice);
        date_of_event.append("Date of Event: "+date);

        time_of_event.append("Time of Event: "+lunch_dinner);
        category_of_event.append("Type {category} of Event: "+event_type_of_user);

        guests_comming.append("Number of Guests Comming: "+noOfGuests);
        //hall_Price.append("Hall Price: "+hall_price); already setted in above qeury

        btn1 =(Button)view.findViewById(R.id.book_marquee);
        btn1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                book_marquee_button_checker+=1;
                if (book_marquee_button_checker <= 1)
                {
                    //NEED query to book user
                    // [NOTE user info. will be store in shared preferences i.e., usermail, name, contact]
                    Map<String,Object> booking = new HashMap<>();
                    //
                    //event_type_of_user = "", pkgs="", progress1_to_str="0", date="", lunch_dinner="", noOfGuests, marquee_OnClick_sent_email="", marquee_OnClick_sent_name="", pkgPrice=""; //all params to pass

                    booking.put("event_type",event_type_of_user);
                    booking.put("package_name",pkgs);
                    booking.put("package_price",pkgPrice);
                    booking.put("guests_coming",noOfGuests);
                    booking.put("Date",date);
                    if(lunch_dinner.equals("Both"))
                    {
                        booking.put("Lunch",uEmail);
                        booking.put("Dinner",uEmail);
                    }
                    else if (lunch_dinner.equals("Lunch"))
                    {
                        booking.put("Lunch",uEmail);
                        booking.put("Dinner","");
                    }
                    else if (lunch_dinner.equals("Dinner"))
                    {
                        booking.put("Dinner",uEmail);
                        booking.put("Lunch","");
                    }
                    booking.put("event_time",lunch_dinner);
                    String tokkens=String.valueOf(tokken);
                    booking.put("token_payed",tokkens);
                    booking.put("name",UserName);
                    booking.put("contact",UserContact);
                    booking.put("purl",UserPurl);
                    booking.put("status","booked");
                    booking.put("email",uEmail); //through shared pref. not qeury like above params

                    if (!event_type_of_user.isEmpty() && !pkgs.isEmpty() && !pkgPrice.isEmpty() && !noOfGuests.isEmpty() && !date.isEmpty() && !lunch_dinner.isEmpty() && tokken != 0 && !UserName.isEmpty() && !UserContact.isEmpty() && !UserPurl.isEmpty() && !uEmail.isEmpty())
                    {
                        dbroot.collection("Bookings").document(marquee_OnClick_sent_email).collection("booked_users").add(booking);

                        //send mail then---------------------------------------------------------------------------------------
                        //String mEmail = email.getText().toString();

                        //set subject of message
                        subject = "Be Ready! New booking arrival";
                        //set  message
                        StringBuilder message = new StringBuilder();

                        message.append("User "+UserName+" has booked your marquee for "+lunch_dinner+" at "+date+"\nThe category of event is ["+event_type_of_user
                                +"] with number of guests comming are "+noOfGuests+" and their selected package is ["+pkgs+"] i.e., package price is "
                                +pkgPrice+" and token [10%] generated for {hall price + total bill} is "+tokken+" where total estimated bill is "
                                +bill);

                        String finalString = message.toString();

                        String mSubject = subject;
                        String mMessage = message.toString().trim();


                        emails.add(marquee_OnClick_sent_email);

                        JavaMailAPIs javaMailAPIs = new JavaMailAPIs(getContext(), emails, mSubject, mMessage);

                        if(!emails.isEmpty())
                        {
                            javaMailAPIs.execute();
                            Toasty.success(getContext(), "Notification Sent Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toasty.error(getContext(), "Error occurred! While sending Notification", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toasty.error(getContext(), "Some error occurred!", Toast.LENGTH_SHORT).show();
                    }
                }

            }//end
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return view;
    }
}