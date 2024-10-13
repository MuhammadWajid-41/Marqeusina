package com.example.Marqeusina;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisasterAlert#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisasterAlert extends Fragment {

    //send booking email to marquee owner
    public ArrayList<String> emails;
    public String subject="", message="";
    //

    FirebaseFirestore dbroot;

    //get marquee's email through shared pref.
    public SharedPreferences marquee_info;
    public String mEmail="";

    public EditText disaster_alert;

    public Button send_disaster_alert;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DisasterAlert() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisasterAlert.
     */
    // TODO: Rename and change types and number of parameters
    public static DisasterAlert newInstance(String param1, String param2) {
        DisasterAlert fragment = new DisasterAlert();
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
        View view = inflater.inflate(R.layout.fragment_disaster_alert, container, false);

        dbroot = FirebaseFirestore.getInstance();

        //send mail to marquee after booking of a user
        emails = new ArrayList<String>();

        //--->Store shared pref.
        marquee_info=(SharedPreferences)this.getActivity().getSharedPreferences("marquee_data",MODE_PRIVATE);

        if(!marquee_info.getString("marquee_email","").isEmpty())
        {
            mEmail =  marquee_info.getString("marquee_email",""); //error here!
           // Toast.makeText(getContext(), "marquee_email is: "+mEmail, Toast.LENGTH_SHORT).show();

            if(!mEmail.isEmpty())
            {
                dbroot.collection("Bookings").document(mEmail).
                        collection("booked_users").whereEqualTo("status","booked")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                        {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                            {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list)
                                {
                                    if (d.getString("status") != null)
                                    {
                                        emails.add(d.getString("email"));
                                        //Toast.makeText(getContext(), "-->"+d.getString("email"), Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toasty.error(getContext(), "An Error Occurred! ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        }

        disaster_alert = view.findViewById(R.id.disaster_alert); //EDITTEXT

        /////////////////////////////////////////////////////////SEND GMAILS/////////////////////////////////////////////////
        send_disaster_alert = view.findViewById(R.id.send_disaster_alert); //BUTTON
        send_disaster_alert.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(disaster_alert.getText().toString().trim().isEmpty())
                {
                    disaster_alert.setError("Please provide any description\nabout disaster!");
                    disaster_alert.requestFocus();
                    return;
                }
                else
                {
                    disaster_alert.setError(null);
                    disaster_alert.clearFocus();

                    subject = "!!!Disaster alert!!!"; //set subject
                    message = disaster_alert.getText().toString().trim();

                    JavaMailAPIs javaMailAPIs = new JavaMailAPIs(getContext(), emails, subject, message);

                    if(!emails.isEmpty())
                    {
                        Toasty.success(getContext(), "Notified users successfully", Toast.LENGTH_SHORT).show();
                        javaMailAPIs.execute();
                    }
                    else
                    {
                        Toasty.error(getContext(), "Notified users UN-Successful!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        /////////////////////////////////////////////////////////SEND GMAIL END/////////////////////////////////////////////////

        return  view;
    }//on create end
}//on main end