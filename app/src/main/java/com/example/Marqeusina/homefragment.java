package com.example.Marqeusina;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//IMPORTS
import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.google.type.LatLng;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
//

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homefragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homefragment extends Fragment implements AdapterView.OnItemSelectedListener, LocationListener {

    public TextView show_filter_error, spinner_tv, spinner_Hall_price_tv;

    public double roundOff=0.0;

    public EditText how_far_ed;
    public String how_far_in_kms = "";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //
    Map<String,Object> items = new HashMap<>();
    //

    RecyclerView recview;
    ArrayList<model> datalist;
    myadapter adapter;

    // Spinner element
    Spinner spinner;
    List<String> categories;

    //location message checker
    public int c = 0;
    //

    //PSGbar
    ProgressDialog progressDialog; //location fetch progress bar
    private int progressStatus = 0;
    private Handler handler = new Handler();

    //PERMISSONS
    LocationManager locationManager;
    public double longitude=0.0, latitude=0.0;
    //

    FirebaseFirestore dbroot;
    public  int f = 0 , ff = 0;
    public  Button filtered_Searach_Btn;

    // below are the latitude and longitude
    // of 2 different locations.
    Double distance;

    //seek bars
    private SeekBar seekbar1,seekbar2;
    //

    //seekbar2 value step
    public int mProgress;
    //

    //Search box by name
    public AutoCompleteTextView searchByName;
    public ImageButton searchByNameBtn;
    public  String marquee_name = "";
    //

    public CheckBox ch1;

    //filter qeury PARAMS
    public  String lat_to_str="0", long_to_str="0", progress1_to_str="0", progress2_to_str="0";
    //

    public homefragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homefragment.
     */
    // TODO: Rename and change types and number of parameters
    public static homefragment newInstance(String param1, String param2) {
        homefragment fragment = new homefragment();
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
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_homefragment, container, false);


        //permissons asking////////////////////////////////////////////////
        // check condition
        if (ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission
                        .ACCESS_FINE_LOCATION)
                == PackageManager
                .PERMISSION_GRANTED)
        {
            // When permission is granted
            // Call method
            getLocation();
        }
        else {
            // When permission is not granted
            // Call method
            requestPermissions(
                    new String[] {
                            Manifest.permission
                                    .ACCESS_FINE_LOCATION},
                    100);
        }
        ///////////////////////////////////////////////////////////////////

        dbroot=FirebaseFirestore.getInstance();

        //AUTOCOMPLETE//
        ArrayList<String> mylist = new ArrayList<String>();
        dbroot.collection("MarqueeOwners").get()
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
                        searchByName.setThreshold(1);
                        //Setting adapter
                        searchByName.setAdapter(arrayAdapter);
                    }
                });
        //END--------//


        //nearby checkbox
        ch1 = (CheckBox) view.findViewById(R.id.ch1);
        TextView tv2 = (TextView) view.findViewById(R.id.tv2); //provide location textview [SHOW ERROR]

        spinner_tv = (TextView) view.findViewById(R.id.spinner_tv);
        spinner_Hall_price_tv = (TextView) view.findViewById(R.id.spinner_Hall_price_tv);

        show_filter_error = view.findViewById(R.id.tv2);
        //filtered_seacrhBtn
        filtered_Searach_Btn = (Button)view.findViewById(R.id.filtered_seacrh);
        how_far_ed = view.findViewById(R.id.how_far_ed);

        show_filter_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_filter_error.setError(null);
                show_filter_error.clearFocus();
            }
        });




        //------> SETON CLICK LISTENER
        filtered_Searach_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //if(!progress1_to_str.equals("0") && !progress2_to_str.equals("0"))
                // {
                //convert latlong to string
                lat_to_str = String.valueOf(latitude);
                long_to_str = String.valueOf(longitude);

                if((!ch1.isChecked() && latitude == 0 && longitude == 0) && progress1_to_str.equals("0") && progress2_to_str.equals("0") && how_far_ed.getText().toString().trim().isEmpty()) //nothing provided!
                {
                    //Toast.makeText(getContext(), "Please utilize any of above fields! \n---->[i.e., Under Filter Search]", Toast.LENGTH_SHORT).show();

                    show_filter_error.setError("Please utilize below fields to filter!");
                    show_filter_error.requestFocus();

                    return;
                }
                else
                {

                    show_filter_error.setError(null);
                    show_filter_error.clearFocus();

                    //send to filteredMarquees act
                    filteredMarquees filter_qeury_params = new filteredMarquees ();

                    Bundle args = new Bundle();



                    if(ch1.isChecked() && latitude != 0 && longitude != 0)
                    {
                        args.putString("marquee_lat", lat_to_str);
                        args.putString("marquee_long", long_to_str);
                    }
                    if(!progress1_to_str.equals("0"))
                    {
                        args.putString("rating_range", progress1_to_str);
                    }
                    if(!progress2_to_str.equals("0"))
                    {
                        args.putString("hall_price_range", progress2_to_str);
                    }
                    if (ch1.isChecked())
                    {
                        tv2.setError(null);
                        tv2.clearFocus();

                        if(!how_far_ed.getText().toString().trim().isEmpty())//distance range provided [not empty]
                        {
                            how_far_ed.setError(null);
                            how_far_ed.clearFocus();

                            how_far_in_kms = how_far_ed.getText().toString().trim();
                            args.putString("how_far_in_kms", how_far_in_kms);
                        }
                        else if(how_far_ed.getText().toString().trim().isEmpty())//distance range NOT provided [empty]
                        {
                            how_far_ed.setError("please provide distance in kms.");
                            how_far_ed.requestFocus();
                            return;
                        }
                    }
                    else if (!ch1.isChecked() && progress1_to_str.equals("0") && progress2_to_str.equals("0"))
                    {
                        Toasty.info(getContext(),"Tap the CHECKBOX\n[Provide your location to search nearby marquees]",Toast.LENGTH_LONG).show();
                        tv2.setError("please provide your current location!");
                        tv2.requestFocus();
                        return;
                    }

                    filter_qeury_params.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, filter_qeury_params, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        ////////////////////////////////////



        ch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {
                if(isChecked)
                {
                    //Toast.makeText(getContext(), "Checked", Toast.LENGTH_SHORT).show();
                    if(latitude == 0 && longitude == 0)
                    {
                        //permissons asking////////////////////////////////////////////////


                        //PARAMS UTILITY lat_to_str, long_to_str, progress1_to_str, progress2_to_str;

                        if (ContextCompat.checkSelfPermission(
                                getActivity(),
                                Manifest.permission
                                        .ACCESS_FINE_LOCATION)
                                == PackageManager
                                .PERMISSION_GRANTED)
                        {
                            // When permission is granted
                            // Call method
                            getLocation();
                        }
                        else {
                            // When permission is not granted
                            // Call method
                            requestPermissions(
                                    new String[] {
                                            Manifest.permission
                                                    .ACCESS_FINE_LOCATION},
                                    100);
                        }
                        ///////////////////////////////////////////////////////////////////


                        //*******************************************************************************************
                        ff += 1;
                        //Toast.makeText(getContext(), "Longitude: " + longitude + " Latitude: " + latitude, Toast.LENGTH_SHORT).show();
                        if(ff == 1)
                        {
                            if(longitude == 0.0 && latitude == 0.0)
                                showProgressDialogWithTitle("System is","fetching location...");
                            else
                                hideProgressDialogWithTitle();
                        }
                    }
                }
            }
        });

        //serachbox by name
        searchByName = (AutoCompleteTextView) view.findViewById(R.id.search_box);
        searchByNameBtn = (ImageButton) view.findViewById(R.id.search_icon);
        ////////////////SETON CLICK LISTENER
        searchByNameBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                marquee_name = searchByName.getText().toString().trim();
                if(marquee_name.isEmpty())
                {
                    searchByName.setError("Please enter any marquee name!");
                    searchByName.requestFocus();
                    return;
                }
                else
                {
                    searchByName.setError(null);
                    searchByName.clearFocus();
                    //fragment intent
                    //Put the value
                    filteredMarquees ldf = new filteredMarquees ();
                    Bundle args = new Bundle();
                    args.putString("marquee name", marquee_name);
                    ldf.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, ldf, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                    //
                }
            }
        });
        ////////////////////////////////////



        //seekbar
        seekbar1 = (SeekBar) view.findViewById(R.id.slider1);// ratings
        seekbar1.incrementProgressBy(1);
        seekbar1.setProgress(0); //starting value
        seekbar1.setMax(10); //ending value 10/2 = 5 [ratings max == 5] ok
        //set sb1 listener///////////////////////////////////////////////////////////
        seekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress1,
                                          boolean fromUser) {
                // TODO Auto-generated method stub

                //Toast.makeText(getContext(), "sb1: " + (double) progress1 / 2, Toast.LENGTH_SHORT).show();

                spinner_tv.setText(String.valueOf((double) progress1 / 2));

                progress1_to_str = String.valueOf((double) progress1 / 2);
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

        seekbar2 = (SeekBar) view.findViewById(R.id.slider2);//price [hall]
        seekbar2.setProgress(0); //starting value
        seekbar2.incrementProgressBy(5000);
        seekbar2.setMax(100000); //ending value 10/2 = 5 [ratings max == 5] ok
        //set sb12 listener

        seekbar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress2,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                progress2 = progress2 / 5000;
                progress2 = progress2 * 5000;

                spinner_Hall_price_tv.setText(String.valueOf(progress2));

                progress2_to_str = String.valueOf(progress2);
                //Toast.makeText(getContext(), "sb1: " + progress2_to_str, Toast.LENGTH_SHORT).show();
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
        /////////////////////////////sb2 end/////////////////////////////////

        progressDialog = new ProgressDialog(getContext());

        /*permissons asking
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },          100 );
        }
        */

        // Spinner element
        spinner = (Spinner) view.findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        categories = new ArrayList<String>();
        categories.add("Popular");
        categories.add("Nearby");
        categories.add("All");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        /////////////////////////////////////////////////////////
        recview=(RecyclerView)view.findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        datalist=new ArrayList<>();
        adapter=new myadapter(datalist);
        recview.setAdapter(adapter);



        ////////////////////////////////////////////////////////


        return view;
    }//CreateView END()


    //PSGBARS
    // Method to show Progress bar###########################################################################
    private void showProgressDialogWithTitle(String title,String substring) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(substring);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();

        // Start Process Operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    try{
                        // This is mock thread using sleep to show progress
                        Thread.sleep(540);
                        progressStatus += 5;
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    // Change percentage in the progress bar
                    handler.post(new Runnable() {
                        public void run() {
                            progressDialog.setProgress(progressStatus);
                        }
                    });
                }
                //hide Progressbar after finishing process
                hideProgressDialogWithTitle();
            }
        }).start();

    }

    // Method to hide/ dismiss Progress bar
    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }
    //############################################################################################################

    //MAP METHODS$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    @SuppressLint("MissingPermission")
    public void getLocation() {
        try {
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, (LocationListener) this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location)
    {

        c++;

        longitude=location.getLongitude();
        latitude=location.getLatitude();

        if (c==1)
        {
            //Toast.makeText(getContext(), "Longitude: " + longitude + " Latitude: " + latitude, Toast.LENGTH_SHORT).show();
            Toasty.info(getContext(), "Your location is fetched. Thanks\nNow we are able to provide you better experience", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$



    //DISTANCE UTILITY FUNCTIONS
    public double CalculationByDistance(double initialLat, double initialLong, double finalLat, double finalLong){
        double latDiff = finalLat - initialLat;
        double longDiff = finalLong - initialLong;
        double earthRadius = 6371; //In Km if you want the distance in km

        double distance = 2*earthRadius*Math.asin(Math.sqrt(Math.pow(Math.sin(latDiff/2.0),2)+Math.cos(initialLat)*Math.cos(finalLat)*Math.pow(Math.sin(longDiff/2),2)));

        return distance;
    }

    public double CalculationsByDistance(double initialLat, double initialLong,
                                         double finalLat, double finalLong){
        int R = 6371; // km (Earth radius)
        double dLat = toRadians(finalLat-initialLat);
        double dLon = toRadians(finalLong-initialLong);
        initialLat = toRadians(initialLat);
        finalLat = toRadians(finalLat);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(initialLat) * Math.cos(finalLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public double toRadians(double deg) {
        return deg * (Math.PI/180);
    }
    /////

    //spinner***********************************
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
    {
        // On selecting a spinner item
        String item = adapterView.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        if(item.equals("Nearby")) {

            //permissons asking////////////////////////////////////////////////
            // check condition
            if (ContextCompat.checkSelfPermission(
                    getActivity(),
                    Manifest.permission
                            .ACCESS_FINE_LOCATION)
                    == PackageManager
                    .PERMISSION_GRANTED)
            {
                // When permission is granted
                // Call method
                getLocation();
            }
            else {
                // When permission is not granted
                // Call method
                requestPermissions(
                        new String[] {
                                Manifest.permission
                                        .ACCESS_FINE_LOCATION},
                        100);
            }
            ///////////////////////////////////////////////////////////////////


            //*******************************************************************************************
            f += 1;
            //Toast.makeText(getContext(), "Longitude: " + longitude + " Latitude: " + latitude, Toast.LENGTH_SHORT).show();
            if(f == 1)
            {
                if(longitude == 0.0 && latitude == 0.0)
                    showProgressDialogWithTitle("System is","fetching location...");
                else
                    hideProgressDialogWithTitle();
            }

            // if(longitude == 0.0 && latitude == 0.0)
            // {
            // onItemSelected(adapterView, view, position, id);
            //}
            //else if(longitude != 0.0 && latitude != 0.0) {
            if(longitude != 0.0 && latitude != 0.0) {
                dbroot.collection("MarqueeOwners").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {

                                    if (d.exists())
                                    {
                                        //qeury
                                        //DISTANCE
                                        Location startPoint = new Location("user");
                                        startPoint.setLatitude(latitude);
                                        startPoint.setLongitude(longitude);

                                    /*convert lat long of usr to 4 dec places
                                    double roundOff_lat = (double) Math.round(latitude * 10000) / 10000;
                                    Toast.makeText(getContext(), "roundOff_lat: "+roundOff_lat, Toast.LENGTH_SHORT).show();
                                    double roundOff_lng = (double) Math.round(longitude * 10000) / 10000;
                                    Toast.makeText(getContext(), "roundOff_lng: "+roundOff_lng, Toast.LENGTH_SHORT).show();
                                    */

                                      //  Toast.makeText(getContext(), "User name: " + d.getString("name"), Toast.LENGTH_SHORT).show();

                                       // Toast.makeText(getContext(), "current user --> Latitude: " + latitude + "\ncurrent user --> Longitude: " + longitude, Toast.LENGTH_SHORT).show();

                                        Location endPoint = new Location("marquee");

                                        //for marquee distance
                                        if (d.get("latitude") != null && d.get("longitude") != null)
                                        {
                                            Number lat_value = (Number) d.get("latitude");
                                            Double converted_latval = lat_value.doubleValue();
                                            //Toast.makeText(getContext(), "converted_latval: "+converted_latval, Toast.LENGTH_SHORT).show();

                                            Number long_value = (Number) d.get("longitude");
                                            Double converted_longval = long_value.doubleValue();
                                            //   Toast.makeText(getContext(), "converted_latval: " + converted_latval + "\nconverted_longval: " + converted_longval, Toast.LENGTH_SHORT).show();
                                            //

                                            //double distance = startPoint.distanceTo(endPoint);
                                            double distance = CalculationsByDistance(latitude, longitude, converted_latval, converted_longval);
                                            // Toast.makeText(getContext(), "distance: " + distance, Toast.LENGTH_SHORT).show();

                                            //ROUND OF UPTO 2 decimal places
                                            if(distance < 1)
                                            {
                                                roundOff = ((double) Math.round(distance * 10) / 10);
                                            }
                                            else
                                            {
                                                roundOff = ((double) Math.round(distance * 10) / 10) + 1;
                                            }
                                            // Toast.makeText(getContext(), "roundOff: " + roundOff, Toast.LENGTH_SHORT).show();


                                            if (roundOff <= 5) {
                                                model obj = d.toObject(model.class);
                                                datalist.add(obj);
                                            }
                                        }

                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
            }//latlong null check end
        }// == nearby condition end
        else if(item.equals("All"))
        {
            dbroot.collection("MarqueeOwners").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                if (d.exists())
                                {
                                    if (d.getString("name") != null && d.getString("email") != null && d.getString("ratings") != null && d.getString("purl") != null)
                                    {
                                        model obj = d.toObject(model.class);
                                        datalist.add(obj);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        else if(item.equals("Popular"))
        {
            dbroot.collection("MarqueeOwners").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                if (d.exists())
                                {
                                    //qeury
                                    //RATINGS

                                    //for marquee distance
                                    if(d.getString("ratings") != null)
                                    {
                                        String rat_value = d.getString("ratings");
                                        //convert ratings to double type
                                        double ratings_converted = Double.parseDouble(rat_value);

                                        if (ratings_converted >= 3.5) {
                                            model obj = d.toObject(model.class);
                                            datalist.add(obj);
                                        }
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        //f += 1;//exception handling [CHECKER]
        //********************************************************************************************
        datalist.clear();
        adapter.notifyDataSetChanged();
        //datalist.clear();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    ///////////////////////////////////////////




}//MAIN END