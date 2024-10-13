package com.example.Marqeusina;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisitedUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisitedUsers extends Fragment {

    //get marquee's email through shared pref.
    public SharedPreferences marquee_info;
    public SharedPreferences.Editor marquee_info_editor;
    public String mEmail="";

    //date check
    //check current date format
    Date todayDate, user_provided_date;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    //


    DatePickerDialog picker;
    //

    RecyclerView recview;
    ArrayList<vmodel> datalist;
    vmyadapter adapter;

    FirebaseFirestore dbroot;
    public  int f = 0 , ff = 0;

    public  String byname="", date="";

    public AutoCompleteTextView search_box;

    public EditText eText;
    public ImageButton search_icon, search_by_date;
    public CheckBox ch1;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VisitedUsers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisitedUsers.
     */
    // TODO: Rename and change types and number of parameters
    public static VisitedUsers newInstance(String param1, String param2) {
        VisitedUsers fragment = new VisitedUsers();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        //--->Store shared pref.
        marquee_info=(SharedPreferences)this.getActivity().getSharedPreferences("marquee_data",MODE_PRIVATE);
        marquee_info_editor=marquee_info.edit();

        if(!marquee_info.getString("marquee_email","").isEmpty())
        {
            mEmail =  marquee_info.getString("marquee_email","");
            //Toast.makeText(getContext(), "marquee_email is: "+mEmail, Toast.LENGTH_SHORT).show();
        }

        dbroot=FirebaseFirestore.getInstance();

        //AUTOCOMPLETE//
        ArrayList<String> mylist = new ArrayList<String>();
        dbroot.collection("Bookings").document(mEmail).collection("booked_users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            if (d.exists())
                            {
                                if (!d.getString("name").equals(""))
                                {
                                    mylist.add(d.getString("name"));
                                }
                            }
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.select_dialog_item, mylist);
                        //Used to specify minimum number of
                        //characters the user has to type in order to display the drop down hint.
                        search_box.setThreshold(1);
                        //Setting adapter
                        search_box.setAdapter(arrayAdapter);
                    }
                });
        //END--------//


        search_box = view.findViewById(R.id.search_box); //search_by_name

        search_icon = view.findViewById(R.id.search_icon);//by name button
        search_by_date = view.findViewById(R.id.search_by_date);//by date button

        ch1 = view.findViewById(R.id.ch1);//by all button

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
                        //Toast.makeText(getContext(), "date"+date, Toast.LENGTH_SHORT).show();

                        try
                        {
                            todayDate = dateFormatter.parse(dateFormatter.format(new Date()));
                            user_provided_date = dateFormatter.parse(date);
                            dateFormatter.format(user_provided_date).equals(dateFormatter.format(todayDate));

                            if(todayDate.compareTo(user_provided_date) <= 0 || todayDate.compareTo(user_provided_date) > 0)
                            {
                                eText.setText(date);
                            }
                            //else
                            //{
                            // eText.setError("Please provide suitable date!");
                            // eText.requestFocus();
                            //  return;
                            //}

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



        /////////////////////////////////////////////////////////
        recview=(RecyclerView)view.findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        datalist=new ArrayList<>();
        adapter=new vmyadapter(getContext(), datalist);
        recview.setAdapter(adapter);

        dbroot=FirebaseFirestore.getInstance();
        ////////////////////////////////////////////////////////

        //searchByNameButton
        //------------------------------------------------------------SETON CLICK LISTENER---------------------------------------------
        final int[] c = {0};
        search_icon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                byname = search_box.getText().toString().trim();
                if(byname.isEmpty())
                {
                    search_box.setError("Please enter any user name!");
                    search_box.requestFocus();
                    return;
                }
                else
                {
                    search_box.setError(null);
                    search_box.clearFocus();
                    /////////////////////////////////////////////////BY_NAME//////////////////////////////////////////////
                    dbroot.collection("Bookings").document(mEmail).collection("booked_users")
                            .whereEqualTo("name",byname)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                            {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list)
                                    {
                                        if (d.getString("status").equals("booked"))
                                        {
                                            vmodel obj = d.toObject(vmodel.class);
                                            datalist.add(obj);
                                        }
                                        //else if (d.getString("status").equals("visited") && c[0] == 0)
                                       // {
                                            //c[0] =1;
                                            //Toasty.error(getContext(),"User Must Be Booked Not Visited!",Toasty.LENGTH_LONG).show();
                                        //}
                                    }
                                    if(datalist.isEmpty())
                                    {
                                        Toasty.error(getContext(),"There are currently no pending Bookings with provided NAME!",Toasty.LENGTH_LONG).show();
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            });

                }
                datalist.clear();
                adapter.notifyDataSetChanged();
            }
        });
        //---------------------------------------------------------------------------------------------------------------------------------------

        //searchByDateButton
        //------------------------------------------------------------SETON CLICK LISTENER---------------------------------------------
        search_by_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(date.isEmpty())
                {
                    eText.setError("Please enter any user name!");
                    eText.requestFocus();
                    return;
                }
                else
                {
                    eText.setError(null);
                    eText.clearFocus();
                    /////////////////////////////////////////////////BY_DATE//////////////////////////////////////////////
                    dbroot.collection("Bookings").document(mEmail).collection("booked_users")
                            .whereEqualTo("Date",date)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                            {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list)
                                    {
                                        if (!d.getString("Date").equals("") && d.getString("status").equals("booked"))
                                        {
                                            //if(d.getString("Date").equals(date))
                                            //{
                                            vmodel obj = d.toObject(vmodel.class);
                                            datalist.add(obj);
                                            //}
                                        }
                                    }

                                    if(datalist.isEmpty())
                                    {
                                        Toasty.error(getContext(),"There are currently no pending Bookings on provided DATE!",Toasty.LENGTH_LONG).show();
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            });

                }
                datalist.clear();
                adapter.notifyDataSetChanged();
            }
        });
        //---------------------------------------------------------------------------------------------------------------------------------------

        //CHECKBOX {SHOW ALL BOOKINGS}
        ch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                ///////////////////////////////////////////////////////////////////////////////////////////////
                if(ch1.isChecked())
                {
                    dbroot.collection("Bookings").document(mEmail).collection("booked_users")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                            {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list)
                                    {
                                        //Toast.makeText(getContext(), "name: "+d.getString("name"), Toast.LENGTH_SHORT).show();
                                        if (!d.getString("name").equals("") && d.getString("status").equals("booked"))
                                       {
                                            vmodel obj = d.toObject(vmodel.class);
                                            datalist.add(obj);
                                        }
                                    }

                                    if(datalist.isEmpty())
                                    {
                                        Toasty.error(getContext(),"There are currently no pending Bookings!",Toasty.LENGTH_LONG).show();
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            });
                }
                datalist.clear();
                adapter.notifyDataSetChanged();
            }
        });

        return  view;
    }//on create end
}//on main end