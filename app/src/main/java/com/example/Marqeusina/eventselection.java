package com.example.Marqeusina;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link eventselection#newInstance} factory method to
 * create an instance of this fragment.
 */
public class eventselection extends Fragment implements AdapterView.OnItemSelectedListener{

    int availabilty_or_bill=0;

    public String event_type_of_user = "", pkgs="", progress1_to_str="0", date="", lunch_dinner="", marquee_OnClick_sent_email="", marquee_OnClick_sent_name="", pkgPrice=""; //all params to pass

    //progress1_to_str="0" means no. of guests coming

    public boolean marquee_availablity_checker;

    //CHECK MARQUEE AVIALABILITY && Generate Bill Buttons
    public Button check_availability, generate_bill;
    //

    //SEEKBAR
    //seek bars
    private SeekBar seekbar1,seekbar2;
    //


    public FirebaseFirestore dbroot;

    //show package description
    public TextView selected_pkg_details, spinner_tv;

    // Spinner element
    public Spinner spinnerE,spinnerP, spinner_date_time;
    List<String> categoriesE, categoriesP, categories_Lunch_time;

    //check current date format
    Date todayDate, user_provided_date;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    //


    DatePickerDialog picker;
    EditText eText;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public eventselection() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment eventselection.
     */
    // TODO: Rename and change types and number of parameters
    public static eventselection newInstance(String param1, String param2) {
        eventselection fragment = new eventselection();
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
        View view = inflater.inflate(R.layout.fragment_eventselection, container, false);

        dbroot=FirebaseFirestore.getInstance();

        //show pkg details [textview]
        selected_pkg_details = (TextView)view.findViewById(R.id.selected_pkg_details);
        selected_pkg_details.setMovementMethod(new ScrollingMovementMethod()); //scrolling view

        spinner_tv = (TextView)view.findViewById(R.id.spinner_tv); //spinner_tv

        //

        //NEED these sent params for this activity
        marquee_OnClick_sent_email = getArguments().getString("marquee email");
        marquee_OnClick_sent_name = getArguments().getString("marquee name");


        //_____________________________________MARQUEE CAPACITY__________________________________//
        //seekbar
        seekbar1 = (SeekBar) view.findViewById(R.id.slider1);// ratings

        dbroot.collection("MarqueeOwners")
                .whereEqualTo("name",marquee_OnClick_sent_name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list)
                        {
                            if(!d.getString("capacity").equals(""))
                            {
                                seekbar1.setMax(Integer.parseInt(d.getString("capacity")));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failure due to technical issues!");
                    }
                });
        //__________________________________________END_________________________________________//

       // Toast.makeText(getContext(), "marquee_OnClick_sent_email: "+marquee_OnClick_sent_email, Toast.LENGTH_SHORT).show();

        //------------------------------------------------ DATE Selection ----------------------------------------------------------//

        eText=(EditText) view.findViewById(R.id.editText1);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        eText.setText("");
                        //convert date to string
                        date = (dayOfMonth + "/" + (monthOfYear + 1) + "/" + year).toString().trim();
                        //oast.makeText(getContext(), "date"+date, Toast.LENGTH_SHORT).show();

                        try
                        {
                            todayDate = dateFormatter.parse(dateFormatter.format(new Date()));
                            user_provided_date = dateFormatter.parse(date);
                            dateFormatter.format(user_provided_date).equals(dateFormatter.format(todayDate));

                            if(todayDate.compareTo(user_provided_date) < 0) //next day constraint
                            {
                                eText.setText(date);
                            }
                            else
                            {
                                eText.setError("Please provide suitable date!");
                                eText.requestFocus();
                                return;
                            }

                                    /*checking...
                                    if (todayDate.compareTo(user_provided_date) > 0) {
                                        Toast.makeText(getContext(), "todayDate > user_provided_date", Toast.LENGTH_SHORT).show();

                                        Log.i("app", "Date1 is after Date2");
                                    } else if (todayDate.compareTo(user_provided_date) < 0) {
                                        Toast.makeText(getContext(), "todayDate < user_provided_date", Toast.LENGTH_SHORT).show();
                                        Log.i("app", "Date1 is before Date2");
                                    } else if (todayDate.compareTo(user_provided_date) == 0) {
                                        Toast.makeText(getContext(), "todayDate == user_provided_date", Toast.LENGTH_SHORT).show();
                                        Log.i("app", "Date1 is equal to Date2");
                                    }
                                    */

                        }
                        catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);
                picker.show();
            }
        });
        //------------------------------------------------ DATE Selection END ----------------------------------------------------------//


        //--------------------------------EVENT SPINNER-----------------------//
        // Spinner element
        spinnerE = (Spinner) view.findViewById(R.id.event_type);

        // Spinner click listener
        spinnerE.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        categoriesE = new ArrayList<String>();
        categoriesE.add("Birthday Party");
        categoriesE.add("Wedding Ceremony");
        categoriesE.add("Naat Khwani");
        categoriesE.add("Aqeeqa");
        categoriesE.add("Political Event");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categoriesE);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerE.setAdapter(dataAdapter);
        //--------------------------------EVENT SPINNER-----------------------//
        //---------------------------------------------Packages spinner---------------------------------------------------//

        // Spinner element
        spinnerP = (Spinner) view.findViewById(R.id.packages);

        // Spinner Drop down elements POPULATE package adapter
        categoriesP = new ArrayList<String>();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterP = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categoriesP);

        // Drop down layout style - list view with radio button
        dataAdapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerP.setAdapter(dataAdapterP);

        //Qeury
        //// Reference to a document in subcollection "packages"
        Task<QuerySnapshot> document = dbroot.collection("Marquee_Packages").document(marquee_OnClick_sent_email).collection("Packages_Info")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots)
                        {
                            String subject = document.getString("name");
                            categoriesP.add(subject);
                        }
                        dataAdapterP.notifyDataSetChanged();
                    }
                });

        spinnerP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // On selecting a spinner item
                pkgs = adapterView.getItemAtPosition(i).toString();

                // Showing selected spinner item
                //Toast.makeText(adapterView.getContext(), "Selected: " + pkgs, Toast.LENGTH_LONG).show();
                selected_pkg_details.setText("");
                Task<QuerySnapshot> documents = dbroot.collection("Marquee_Packages").document(marquee_OnClick_sent_email).collection("Packages_Info")
                        .whereEqualTo("name",pkgs)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(QueryDocumentSnapshot document: queryDocumentSnapshots)
                                {
                                    String pkgName = document.getString("name");
                                    String pkgDescription = document.getString("description");
                                    pkgPrice = document.getString("price");
                                    selected_pkg_details.append("Package Name: "+pkgName+"\n"+"Package Description: "
                                            +pkgDescription+"\n"+"Package Price: "+pkgPrice+"\n");
                                }
                            }
                        });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //-----------------------------------------------pkg Spinner------------------------------------------------------//

        //-----------------------------------------------DATE [lunch/Dinner/Both]------------------------------------------------------//

        spinner_date_time = (Spinner) view.findViewById(R.id.date_time_spinner);
        // Spinner click listener
        // spinner_date_time.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        categories_Lunch_time = new ArrayList<String>();
        categories_Lunch_time.add("Lunch");
        categories_Lunch_time.add("Dinner");
        categories_Lunch_time.add("Both");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterLunchDinnerBoth = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories_Lunch_time);

        // Drop down layout style - list view with radio button
        dataAdapterLunchDinnerBoth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_date_time.setAdapter(dataAdapterLunchDinnerBoth);

        spinner_date_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // On selecting a spinner item
                lunch_dinner = adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //-----------------------------------------------DATE [lunch/dinner/both]-------------------------------------------//

        //-------------------------------------------------SEEKBAR------------------------------------------------------------//
        //seekbar
        //seekbar1 = (SeekBar) view.findViewById(R.id.slider1);// ratings
        seekbar1.incrementProgressBy(50);
        seekbar1.setProgress(0); //starting value
        //set sb1 listener///////////////////////////////////////////////////////////
        seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress1,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                progress1 = progress1 / 50;
                progress1 = progress1 * 50;

                progress1_to_str = String.valueOf(progress1);

                spinner_tv.setText(String.valueOf(progress1));
                //Toast.makeText(getContext(), "progress1_to_str: "+progress1_to_str, Toast.LENGTH_SHORT).show();
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
        //-------------------------------------------------------------------------------------------------------------------//

        //--------------------------------------------------------Check marquee availability-------------------------------//
        check_availability=(Button) view.findViewById(R.id.check_availability);
        check_availability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                availabilty_or_bill=0;
                check_marquee_availabilty(availabilty_or_bill);
            }//button onclick end
        });
        //-----------------------------------------------------------------------------------------------------------------//

        //-------------------------------------------------Bill Generation-------------------------------------------------//
        generate_bill=(Button) view.findViewById(R.id.Generate_Bill);
        generate_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                availabilty_or_bill=1;
                check_marquee_availabilty(availabilty_or_bill);
            }
        });
        //-----------------------------------------------------------------------------------------------------------------//
        return  view;
    }//ONCREATE END

    //CHECK MARQUEE AVAILABILITY FUNCTION
    public void check_marquee_availabilty(int availabilty_or_bill)
    {
        //Toast.makeText(getContext(), "availabilty_or_bill: "+availabilty_or_bill, Toast.LENGTH_SHORT).show();
        final int[] counter = {0};

        if(date.isEmpty())
        {
            eText.setError("Please provide any date!");
            eText.requestFocus();
            // return;
        }
        else if(!date.isEmpty())
        {
            eText.setError(null);
            eText.clearFocus();

            /////////////////////////////////////////////////AVAILABILITY////////////////////////////////////////////////////////////////////////////////
            dbroot.collection("Bookings").document(marquee_OnClick_sent_email).collection("booked_users")
                    .whereEqualTo("Date",date)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().size() > 0)
                                {
                                    for (DocumentSnapshot document : task.getResult()) //if date in case exists in marquees [mean an event is already there on the user seleced data
                                    {
                                            //user date time selected
                                            if(lunch_dinner.equals("Lunch"))
                                            {
                                                String lunch_check = document.getString("Lunch");
                                                if(lunch_check.equals(""))
                                                {
                                                    counter[0] = 1;
                                                    marquee_availablity_checker=true;
                                                    //slot available [or book user here]
                                                    // Toast.makeText(getContext(), "Marquee is available for [Lunch] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    counter[0] = 2;
                                                    marquee_availablity_checker=false;
                                                    //  Toast.makeText(getContext(), "Marquee is not available for [Lunch] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else if(lunch_dinner.equals("Dinner"))
                                            {
                                                String dinner_check = document.getString("Dinner");
                                                if(dinner_check.equals(""))
                                                {
                                                    counter[0] = 3;
                                                    marquee_availablity_checker=true;
                                                    //slot available [or book user here]
                                                    // Toast.makeText(getContext(), "Marquee IS available for [Dinner] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    counter[0] = 4;
                                                    marquee_availablity_checker=false;
                                                    // Toast.makeText(getContext(), "Marquee IS NOT available for [Dinner] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else if(lunch_dinner.equals("Both"))
                                            {
                                                String lunch_check = document.getString("Lunch");
                                                String dinner_check = document.getString("Dinner");
                                                if(dinner_check.equals("") && lunch_check.equals(""))
                                                {
                                                    counter[0] = 5;
                                                    marquee_availablity_checker=true;
                                                    //slot available [or book user here]
                                                    //  Toast.makeText(getContext(), "Marquee IS available for both [Lunch & Dinner] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    counter[0] = 6;
                                                    marquee_availablity_checker=false;
                                                    // Toast.makeText(getContext(), "Marquee IS NOT available for both [Lunch & Dinner] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                    }//for loop documents fetch end


                                    /////////////////////////////////////////////////////////CHECK ONLY AVAILABILITY//////////////////////////////////

                                    /*if(availabilty_or_bill == 0)
                                    {
                                        Toast.makeText(getContext(), "CHECK ONLY availability", Toast.LENGTH_SHORT).show();
                                        if(marquee_availablity_checker == false)
                                        {
                                            Toast.makeText(getContext(), "marquee_availablity_checker FALSE", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(getContext(), "marquee_availablity_checker TRUE!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }*/

                                    //in case of availability check only
                                    if(availabilty_or_bill == 0)
                                    {
                                        //Toast.makeText(getContext(), "--->counter[0]: "+counter[0], Toast.LENGTH_SHORT).show();
                                        if(counter[0] == 1)
                                        {
                                            Toasty.success(getContext(), "Marquee is available for [Lunch] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                        }
                                        else if(counter[0] == 2)
                                        {
                                            Toasty.error(getContext(), "Marquee is not available for [Lunch] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                        }
                                        else if(counter[0] == 3)
                                        {
                                            Toasty.success(getContext(), "Marquee IS available for [Dinner] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                        }
                                        else if(counter[0] == 4)
                                        {
                                            Toasty.error(getContext(), "Marquee IS NOT available for [Dinner] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                        }
                                        else if(counter[0] == 5)
                                        {
                                            Toasty.success(getContext(), "Marquee IS available for both [Lunch & Dinner] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                        }
                                        else if(counter[0] == 6)
                                        {
                                            Toasty.error(getContext(), "Marquee IS NOT available for both [Lunch & Dinner] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

                                    //-------------------------------------------------------BILL GENERATION----------------------------------//

                                    //in case of bill generation
                                    /*if(availabilty_or_bill == 1)
                                    {
                                        if(marquee_availablity_checker == true)
                                        {
                                            Toast.makeText(getContext(), "marquee_availablity_checker TRUE", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(getContext(), "marquee_availablity_checker FALSE!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }*/
                                    if(availabilty_or_bill == 1 &&  marquee_availablity_checker == true)
                                    {
                                        //get all params here together--------------------------------------------------[params]

                                        //-->event_type_of_user = "", pkgs="", progress1_to_str="0", date="", lunch_dinner="", marquee_OnClick_sent_email="", pkgPrice=""; //all params to pass
                                        //-->progress1_to_str="0" means no. of guests coming

                                        //------------------------------------------------------------------------------
                                        //create bill generation intent
                                        if(progress1_to_str.equals("0"))
                                        {
                                            Toasty.error(getContext(), "please provide number of guests coming!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        else
                                        {
                                            Toasty.success(getContext(), "Marquee IS AVAILABLE on selected date and event", Toast.LENGTH_SHORT).show();

                                            bill_generation ldf = new bill_generation();
                                            Bundle args = new Bundle();
                                            args.putString("marquee name", marquee_OnClick_sent_name);
                                            args.putString("marquee email", marquee_OnClick_sent_email);
                                            args.putString("event_type_of_user", event_type_of_user);

                                            args.putString("package", pkgs);
                                            args.putString("guests coming", progress1_to_str);
                                            args.putString("event_date", date);
                                            args.putString("lunch_dinner", lunch_dinner);
                                            args.putString("pkgPrice", pkgPrice);

                                            ldf.setArguments(args);

                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.container, ldf, "findThisFragmentalso")
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    }
                                    else if(availabilty_or_bill == 1 &&  marquee_availablity_checker == false)
                                    {
                                        Toasty.error(getContext(), "Marquee IS NOT AVAILABLE on selected date and event!", Toast.LENGTH_SHORT).show();
                                    }

                                    //--------------------------------------------------------------------------------------------------------//


                                }
                                else if(task.getResult().size() <= 0) //date doesn't exists
                                {
                                    Toasty.success(getContext(), "Marquee IS available for both [Lunch & Dinner] on selected date i.e., "+date, Toast.LENGTH_SHORT).show();
                                    if(availabilty_or_bill == 1)
                                    {
                                        //get all params here together--------------------------------------------------[params]

                                        //-->event_type_of_user = "", pkgs="", progress1_to_str="0", date="", lunch_dinner="", marquee_OnClick_sent_email="", pkgPrice=""; //all params to pass
                                        //-->progress1_to_str="0" means no. of guests coming

                                        //------------------------------------------------------------------------------
                                        //create bill generation intent
                                        if(progress1_to_str.equals("0"))
                                        {
                                            Toasty.error(getContext(), "please provide number of guests coming!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        else
                                        {
                                            //Toasty.success(getContext(), "Marquee IS AVAILABLE on selected date and event", Toast.LENGTH_SHORT).show();

                                            bill_generation ldf = new bill_generation();
                                            Bundle args = new Bundle();
                                            args.putString("marquee name", marquee_OnClick_sent_name);
                                            args.putString("marquee email", marquee_OnClick_sent_email);
                                            args.putString("event_type_of_user", event_type_of_user);

                                            args.putString("package", pkgs);
                                            args.putString("guests coming", progress1_to_str);
                                            args.putString("event_date", date);
                                            args.putString("lunch_dinner", lunch_dinner);
                                            args.putString("pkgPrice", pkgPrice);

                                            ldf.setArguments(args);

                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.container, ldf, "findThisFragmentalso")
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    }
                                    //--------------------------------------------------------------------------------------------------------//

                                }
                            }
                            else
                            {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                Toasty.error(getContext(), "A system ERROR occurred, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            ////////////////////////////////////////////////////////////////////////////////////////////////

        }//else [date not empty end condition]

    }//function end

    //----------------------------------------------------------------event spinner-----------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        // On selecting a spinner item
        event_type_of_user = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    //----------------------------------------------------------------event spinner-----------------------------------------
}//MAIN END