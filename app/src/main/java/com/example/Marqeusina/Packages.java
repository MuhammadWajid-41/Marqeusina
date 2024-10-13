package com.example.Marqeusina;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
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
 * Use the {@link Packages#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Packages extends Fragment {

    // Spinner element
    public Spinner spinnerP;
    List<String> categoriesP;

    public Spinner spinnerPp;
    List<String> categoriesPp;

    public String pkgs="",pkgss="";

    //
    Map<String,Object> items = new HashMap<>();
    //

    //get marquee's email through shared pref.
    public SharedPreferences marquee_info;
    public String mEmail="";

    //firebase
    FirebaseFirestore dbroot;

    //show package description [textview]
    public TextView selected_pkg_details, tv3;

    //all edit texts

    public AutoCompleteTextView pkg_name;

    public EditText  pkg_price, pkg_description;

    //all buttons
    public Button delete_pkg, pkg_details, add_pkg, update_pkg;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Packages() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Packages.
     */
    // TODO: Rename and change types and number of parameters
    public static Packages newInstance(String param1, String param2) {
        Packages fragment = new Packages();
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
        View view = inflater.inflate(R.layout.fragment_packages, container, false);

        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        //--->Store shared pref.
        marquee_info=(SharedPreferences)this.getActivity().getSharedPreferences("marquee_data",MODE_PRIVATE);

        if(!marquee_info.getString("marquee_email","").isEmpty())
        {
            mEmail =  marquee_info.getString("marquee_email","");
          //  Toast.makeText(getContext(), "marquee_email is: "+mEmail, Toast.LENGTH_SHORT).show();
        }

        //INITIALIZE-------------------------------------------------------------------------------------------
        //EditText----> search_box, ed_pkg_details, pkg_name, pkg_price, pkg_description;

        //_____________________________________________AUTOCOMPLETE_DELETE_________________________________________________________//
        dbroot=FirebaseFirestore.getInstance();

        //AUTOCOMPLETE//

        ArrayList<String> mylist1 = new ArrayList<String>(); //View pkg details by name



        dbroot.collection("Marquee_Packages").document(mEmail).collection("Packages_Info")
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
                                    mylist1.add(d.getString("name"));
                                }
                            }
                        }

                        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.select_dialog_item, mylist1);

                        //Used to specify minimum number of
                        //characters the user has to type in order to display the drop down hint.
                        pkg_name.setThreshold(1);
                        //Setting adapter
                        pkg_name.setAdapter(arrayAdapter1);
                    }
                });
        //END--------//

        pkg_name = view.findViewById(R.id.pkg_name); //pkg name edittext for ---> ADD/UPDATE button
        //_____________________________________________ AUTOCOMPLETE DELETE_END___________________________________________________//


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

        //PACKAGES1-------------------------------
        // Spinner element
        spinnerPp = (Spinner) view.findViewById(R.id.packages1);

        // Spinner Drop down elements POPULATE package adapter
        categoriesPp = new ArrayList<String>();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterPp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categoriesPp);

        // Drop down layout style - list view with radio button
        dataAdapterPp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerPp.setAdapter(dataAdapterP);
        //----------------------------------------

        //Qeury
        //// Reference to a document in subcollection "packages"
        Task<QuerySnapshot> document = dbroot.collection("Marquee_Packages").document(mEmail).collection("Packages_Info")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots)
                        {
                            String subjects = document.getString("name");
                            categoriesP.add(subjects);
                            categoriesPp.add(subjects);
                        }
                        dataAdapterP.notifyDataSetChanged();
                        dataAdapterPp.notifyDataSetChanged();
                    }
                });

        spinnerP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // On selecting a spinner item
                pkgs = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerPp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // On selecting a spinner item
                pkgss = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //-----------------------------------------------pkg Spinner------------------------------------------------------//


        pkg_price = view.findViewById(R.id.pkg_price);
        pkg_description = view.findViewById(R.id.pkg_description);

        //TEXTVIEW
        //show pkg details [textview]
        selected_pkg_details = (TextView)view.findViewById(R.id.selected_pkg_details);
        selected_pkg_details.setMovementMethod(new ScrollingMovementMethod()); //scrolling view
        tv3 = (TextView)view.findViewById(R.id.tv3); //for update error

        //Buttons---> delete_pkg, pkg_details, add_pkg, update_pkg;
        delete_pkg = view.findViewById(R.id.delete_pkg); //delete pkg
        pkg_details = view.findViewById(R.id.pkg_details); //view pkg
        add_pkg = view.findViewById(R.id.add_pkg); //add pkg
        update_pkg = view.findViewById(R.id.update_pkg); //update pkg
        //-----------------------------------------------------------------------------------------------------

        //Delete Button listener
        delete_pkg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(pkgs.equals("")) //package name check
                {
                    delete_pkg.setError("Please enter any package name!");
                    delete_pkg.requestFocus();
                    return;
                }
                else
                {
                    //++++++++++++++++//
                    //////////////////////////////////////ALERT DIALOGUE
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setMessage("Do you want to delete selected package?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    try
                                    {
                                        delete_pkg.setError(null);
                                        delete_pkg.clearFocus();
                                        /////////////////////////////////////////////////PKG-DELETE//////////////////////////////////////////////
                                        dbroot.collection("Marquee_Packages").document(mEmail)
                                                .collection("Packages_Info")
                                                .whereEqualTo("name",pkgs)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if(task.getResult().size() > 0)
                                                            {
                                                                for (DocumentSnapshot d : task.getResult())
                                                                {
                                                                    String docID = d.getId();
                                                                    //Toast.makeText(getContext(), "package name by doc: "+d.getString("name"), Toast.LENGTH_SHORT).show();
                                                                    dbroot.collection("Marquee_Packages").document(mEmail).
                                                                            collection("Packages_Info").document(docID).delete();
                                                                    Toasty.success(getContext(), "Package DELETED Successfully", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            else if(task.getResult().size() <= 0)
                                                            {
                                                                Toasty.error(getContext(), "No such package exists!", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                        else
                                                        {
                                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                                            Toasty.error(getContext(), "A system ERROR occurred, please try again", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    catch (SecurityException s)
                                    {
                                        // show() method display the toast with
                                        // exception message.
                                        Toasty.error(getContext(), "An error occurred", Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    //++++++++++++++++//

                }
            }
        });
        //Delete Button listener end

        //VIEW Button listener
        pkg_details.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(pkgss.equals("")) //package name check
                {
                    pkg_details.setError("Please enter any package name!");
                    pkg_details.requestFocus();
                    return;
                }
                else
                {
                    pkg_details.setError(null);
                    pkg_details.clearFocus();
                    /////////////////////////////////////////////////VIEW_PKG//////////////////////////////////////////////
                    dbroot.collection("Marquee_Packages").document(mEmail)
                            .collection("Packages_Info")
                            .whereEqualTo("name",pkgss)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().size() > 0)
                                        {
                                            for (DocumentSnapshot d : task.getResult())
                                            {
                                                Log.d(TAG, "Package already exists, add new one please!");
                                                selected_pkg_details.setText("");
                                                selected_pkg_details.append("-->Package Name: "+d.getString("name")
                                                        +"\n-->Package Price: "+d.getString("price")
                                                        +"\n-->Package Description: "+d.getString("description"));
                                              //  ed_pkg_details.setText("");
                                            }
                                        }
                                        else if(task.getResult().size() <= 0)
                                        {
                                            Toasty.error(getContext(), "No such package exists!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                        Toasty.error(getContext(), "A system ERROR occurred, please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        //VIEW Button listener end

        //Add Button listener
        add_pkg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(pkg_name.getText().toString().trim().isEmpty()) //package name check
                {
                    pkg_name.setError("Please enter any package name!");
                    pkg_name.requestFocus();
                    return;
                }
                if(pkg_price.getText().toString().trim().isEmpty()) //pkg_price check
                {
                    pkg_price.setError("Please enter package price!");
                    pkg_price.requestFocus();
                    return;
                }
                if(pkg_description.getText().toString().trim().isEmpty()) //pkg_description check
                {
                    pkg_description.setError("Please enter package description!");
                    pkg_description.requestFocus();
                    return;
                }
                if(!pkg_name.getText().toString().trim().isEmpty() && !pkg_price.getText().toString().trim().isEmpty() && !pkg_description.getText().toString().trim().isEmpty())
                {
                    pkg_name.setError(null);
                    pkg_name.clearFocus();

                    pkg_price.setError(null);
                    pkg_price.clearFocus();

                    pkg_description.setError(null);
                    pkg_description.clearFocus();
                    /////////////////////////////////////////////////PKG ADD//////////////////////////////////////////////
                    items.clear();
                    if(!pkg_name.getText().toString().trim().isEmpty())
                        items.put("name",pkg_name.getText().toString().trim());
                    if(!pkg_price.getText().toString().trim().isEmpty())
                        items.put("price",pkg_price.getText().toString().trim());
                    if(!pkg_description.getText().toString().trim().isEmpty())
                        items.put("description",pkg_description.getText().toString().trim());
                    //---->query to add

                    dbroot.collection("Marquee_Packages").document(mEmail)
                            .collection("Packages_Info")
                            .whereEqualTo("name",pkg_name.getText().toString().trim())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().size() > 0)
                                        {
                                            for (DocumentSnapshot document : task.getResult())
                                            {
                                                Log.d(TAG, "Package already exists, add new one please!");
                                                Toasty.error(getContext(), "Package already exists, add new one please!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else if(task.getResult().size() <= 0)
                                        {
                                            //for (DocumentSnapshot document : task.getResult())
                                            //{
                                            String updating_pkgs_name = pkg_name.getText().toString().trim();
                                            Log.d(TAG, "Package added successfully");//add pkg
                                            Toasty.success(getContext(), "Package Added Successfully", Toast.LENGTH_SHORT).show();
                                            dbroot.collection("Marquee_Packages").document(mEmail)
                                                    .collection("Packages_Info").
                                                    document(updating_pkgs_name).set(items);
                                            pkg_name.setText("");
                                            pkg_price.setText("");
                                            pkg_description.setText("");
                                            //}

                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                        Toasty.error(getContext(), "A system ERROR occurred, please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        //ADD pkg Button listener end

        //UPDATE Button listener
        update_pkg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(pkg_name.getText().toString().trim().isEmpty()) //package name check
                {
                    pkg_name.setError("Please enter any package name\n which has to be updated");
                    pkg_name.requestFocus();
                    return;
                }
                else
                {
                    if(pkg_price.getText().toString().trim().isEmpty() && pkg_description.getText().toString().trim().isEmpty()) //pkg_price check
                    {
                        tv3.setError("Please provide any update data in below fields!");
                        tv3.requestFocus();
                        return;
                    }
                }
                if(pkg_name.getText().toString().trim().isEmpty() && pkg_price.getText().toString().trim().isEmpty() && pkg_description.getText().toString().trim().isEmpty())
                {
                    Toasty.error(getContext(), "Please provide any details to update!", Toast.LENGTH_SHORT).show();
                }
                if((!pkg_name.getText().toString().trim().isEmpty() && !pkg_price.getText().toString().trim().isEmpty()) || (!pkg_name.getText().toString().trim().isEmpty() && !pkg_description.getText().toString().trim().isEmpty()))
                {
                    pkg_name.setError(null);
                    pkg_name.clearFocus();

                    tv3.setError(null);
                    tv3.clearFocus();

                    /////////////////////////////////////////////////PKG UPDATE//////////////////////////////////////////////

                    items.clear();
                    if(!pkg_name.getText().toString().trim().isEmpty())
                        items.put("name",pkg_name.getText().toString().trim());
                    if(!pkg_price.getText().toString().trim().isEmpty())
                        items.put("price",pkg_price.getText().toString().trim());
                    if(!pkg_description.getText().toString().trim().isEmpty())
                        items.put("description",pkg_description.getText().toString().trim());
                    //---->query to update

                    dbroot.collection("Marquee_Packages").document(mEmail)
                            .collection("Packages_Info")
                            .whereEqualTo("name",pkg_name.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().size() > 0)
                                        {
                                            for (DocumentSnapshot document : task.getResult())
                                            {
                                                String updating_pkg_name = pkg_name.getText().toString().trim();
                                                Log.d(TAG, "Package already exists [GOOD to update]");//update
                                                Toasty.success(getContext(), "Package Updated Successfully", Toast.LENGTH_SHORT).show();
                                                dbroot.collection("Marquee_Packages").document(mEmail)
                                                        .collection("Packages_Info").
                                                        document(updating_pkg_name).set(items, SetOptions.merge());
                                                pkg_name.setText("");
                                                pkg_price.setText("");
                                                pkg_description.setText("");
                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "Package Doesn't Exists! try again with other name");
                                            Toasty.error(getContext(), "Package Doesn't Exists! try again with other name", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                        Toasty.error(getContext(), "A system ERROR occurred, please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });//UPDATE Button listener end


        return view;
    } //on create end

} //on main end

//pkg update qeury
/*dbroot.collection("Marquee_Packages").document(mEmail)
                            .collection("Packages_Info")
                            .whereEqualTo("name",pkg_name.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().size() > 0)
                                        {
                                            for (DocumentSnapshot document : task.getResult())
                                            {
                                                String updating_pkg_name = pkg_name.getText().toString().trim();
                                                Log.d(TAG, "Package already exists [GOOD to update]");//update
                                                Toast.makeText(getContext(), "Package Updated Successfully", Toast.LENGTH_SHORT).show();
                                                dbroot.collection("Marquee_Packages").document(mEmail)
                                                        .collection("Packages_Info").
                                                        document(updating_pkg_name).set(items, SetOptions.merge());
                                                pkg_name.setText("");
                                                pkg_price.setText("");
                                                pkg_description.setText("");
                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "Package Doesn't Exists! try again with other name");
                                            Toast.makeText(getContext(), "Package Doesn't Exists! try again with other name", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                        Toast.makeText(getContext(), "A system ERROR occurred, please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });*/
//pkg add
/* dbroot.collection("Marquee_Packages").document(mEmail)
                            .collection("Packages_Info")
                            .whereEqualTo("name",pkg_name.getText().toString().trim())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().size() > 0)
                                        {
                                            for (DocumentSnapshot document : task.getResult())
                                            {
                                                Log.d(TAG, "Package already exists, add new one please!");
                                                Toast.makeText(getContext(), "Package already exists, add new one please!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else
                                        {
                                            for (DocumentSnapshot document : task.getResult())
                                            {
                                                String updating_pkgs_name = pkg_name.getText().toString().trim();
                                                Log.d(TAG, "Package added successfully");//add pkg
                                                Toast.makeText(getContext(), "Package Added Successfully", Toast.LENGTH_SHORT).show();
                                                dbroot.collection("Marquee_Packages").document(mEmail)
                                                        .collection("Packages_Info").
                                                        document(updating_pkgs_name).set(items);
                                                pkg_name.setText("");
                                                pkg_price.setText("");
                                                pkg_description.setText("");
                                            }

                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                        Toast.makeText(getContext(), "A system ERROR occurred, please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });*/