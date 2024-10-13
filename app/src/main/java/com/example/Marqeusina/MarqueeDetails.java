package com.example.Marqeusina;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarqueeDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarqueeDetails extends Fragment implements LocationListener {

    //location_marker
    public ImageView location_marker;
    double marquee_lat=0.0, marquee_long=0.0;
    //

    //CONTACT cell no. of marquee
    public String number = "";

    //FOR USER COMMENTS ON MARQUEES
    RecyclerView crecview;
    ArrayList<cmodel> cdatalist;
    cmyadapter cadapter;
    //

    public  int f=0;
    //PERMISSONS DISTANCE LATLONG
    LocationManager locationManager;
    public double longitude=0.0, latitude=0.0;
    Double distance;
    //location message checker
    public int c = 0;
    //

    ImageSlider mainslider;
    FirebaseFirestore dbroot = FirebaseFirestore.getInstance();

    String marquee_OnClick_sent_name="", marquee_OnClick_sent_email="";

    String Mnames="", Mrating="", Mservice, MdistanceVal, Muser_comment;
    public  TextView Mname, Mratings, Mservices, MdistanceValue, Mdistancetv, capacity_value; //, Muser_comments
    public Button event_selection_activity;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MarqueeDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarqueeDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static MarqueeDetails newInstance(String param1, String param2) {
        MarqueeDetails fragment = new MarqueeDetails();
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
        // Inflate the layout for this fragment  String value = getArguments().getString("YourKey");
        View view = inflater.inflate(R.layout.fragment_marquee_details, container, false);

        //NEED these sent params for this activity
        marquee_OnClick_sent_name = getArguments().getString("marquee name");
        marquee_OnClick_sent_email = getArguments().getString("marquee email");
        //

        event_selection_activity = (Button)view.findViewById(R.id.event_selection_activity);
        event_selection_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!marquee_OnClick_sent_email.isEmpty())
                {
                    eventselection ldf = new eventselection ();
                    Bundle args = new Bundle();
                    args.putString("marquee email", marquee_OnClick_sent_email);
                    args.putString("marquee name",marquee_OnClick_sent_name);
                    ldf.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, ldf, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                }
                else
                {
                    Toasty.error(getContext(), "Some error occurred!", Toast.LENGTH_SHORT).show();
                }

            }
        });



        //MARQUEE CONTACT GET QEURY--------------------------------------------------------------------------------------------------
        if(number.isEmpty()) {
            DocumentReference ContactdocRef = dbroot.collection("MarqueeOwners").document(marquee_OnClick_sent_email);
            ContactdocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            number = document.getString("contact").toString().trim(); //user name
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
        //--------------------------------------------------------------------------------------------------

        //////////////////////////////////FOR USER COMMENTS ON MARQUEES ///////////////////////////////////

        crecview=(RecyclerView)view.findViewById(R.id.recview);
        crecview.setLayoutManager(new LinearLayoutManager(getContext()));
        cdatalist=new ArrayList<>();
        cadapter=new cmyadapter(cdatalist);
        crecview.setAdapter(cadapter);
        //

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

        Mdistancetv = (TextView)view.findViewById(R.id.distance);
        Mdistancetv.setPaintFlags(Mdistancetv.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        Mname = (TextView)view.findViewById(R.id.marquee_name);
        capacity_value = (TextView)view.findViewById(R.id.capacity_value);
        Mratings = (TextView)view.findViewById(R.id.ratings);
        Mservices = (TextView)view.findViewById(R.id.marquee_services);

        Mservices.setMovementMethod(new ScrollingMovementMethod()); //scrolling view
        MdistanceValue = (TextView)view.findViewById(R.id.distance_value);
        // Muser_comments = (TextView)view.findViewById(R.id.marquee_related_user_comments);

        // comments_show_btn = (Button)view.findViewById(R.id.comments_btn);
        Button contact_marquee = (Button)view.findViewById(R.id.contact_marquee); //CONTACT MARQUEE INTENT
        if(contact_marquee != null)
        {
            contact_marquee.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(!number.isEmpty())
                    {
                        //////////////////////////////////////ALERT DIALOGUE
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                        builder1.setMessage("Do you want to use your phone's dial pad? if yes then tap {yes}\n[Note]:Tap your phone's back button to come back to this application again from the dial pad application");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        // Use format with "tel:" and phoneNumber created is
                                        // stored in u.
                                        Uri u = Uri.parse("tel:" + number.toString().trim());

                                        // Create the intent and set the data for the
                                        // intent as the phone number.
                                        Intent i = new Intent(Intent.ACTION_DIAL, u);

                                        try
                                        {
                                            // Launch the Phone app's dialer with a phone
                                            // number to dial a call.
                                            startActivity(i);
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

                    }
                }
            });
        }


        //Retrieve the sent marquee name, email value
        //marquee_OnClick_sent_name = getArguments().getString("marquee name");
        //marquee_OnClick_sent_email = getArguments().getString("marquee email"); //already retrieved above for marquee contact

        //*******************************************************************************************************


        mainslider=(ImageSlider)view.findViewById(R.id.image_slider);
        final List<SlideModel> remoteimages=new ArrayList<>();

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
                            //fill other textviews here [marquee name, distance etc.]
                            //Mnames="", Mrating="", Mservice, MdistanceVal, Muser_comment;
                            //
                            if(!d.getString("name").equals(""))
                                Mname.setText(d.getString("name").toString().trim());
                            if(!d.getString("ratings").equals(""))
                                Mratings.setText(d.getString("ratings").toString().trim());
                            if(!d.getString("capacity").equals(""))
                            {
                                capacity_value.setText(d.getString("capacity").toString().trim());
                            }


                            if(!d.getString("image0").equals(""))
                            {
                                remoteimages.add(new SlideModel(d.getString("image0").toString().trim(), ScaleTypes.FIT));
                            }
                            if(!d.getString("image1").equals(""))
                            {
                                remoteimages.add(new SlideModel(d.getString("image1").toString().trim(), ScaleTypes.FIT));
                            }
                            if(!d.getString("image2").equals(""))
                            {
                                remoteimages.add(new SlideModel(d.getString("image2").toString().trim(), ScaleTypes.FIT));
                            }

                            if(!remoteimages.isEmpty())
                            {
                                mainslider.setImageList(remoteimages,ScaleTypes.FIT);
                                mainslider.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onItemSelected(int i) {
                                        //Toast.makeText(getContext(),remoteimages.get(i).getTitle().toString(),Toast.LENGTH_SHORT).show();
                                    }
                                });
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

        //*******************************************************************************************************

        //Toast.makeText(getContext(), "marquee name: "+marquee_OnClick_sent_name+"\nmarquee email"+marquee_OnClick_sent_email, Toast.LENGTH_SHORT).show();

        //---------------------------------------------------------------------------------------------//
        //textview listener
        Mdistancetv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                f+=1;
                if(longitude == 0.0 && latitude == 0.0 && f == 1)
                {
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

                }
                else if(longitude != 0.0 && latitude != 0.0) {
                    dbroot.collection("MarqueeOwners")
                            .whereEqualTo("name",marquee_OnClick_sent_name)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {

                                        //for marquee distance
                                        Number lat_value = (Number) d.get("latitude");
                                        Double converted_latval = lat_value.doubleValue();

                                        Number long_value = (Number) d.get("longitude");
                                        Double converted_longval = long_value.doubleValue();
                                        //

                                        String di = getDistance(latitude, longitude, converted_latval, converted_longval);
                                        //Toast.makeText(getContext(), "parsedDistance: "+di, Toast.LENGTH_SHORT).show();

                                        double distance = CalculationsByDistance(latitude, longitude, converted_latval, converted_longval);
                                        //Toast.makeText(getContext(), "distance: " + distance, Toast.LENGTH_SHORT).show();

                                        String formatted_Rat_Value = String.format("%.1f", distance);//rounding of to one decimal place

                                        double converted_distance_from_str_to_doubl = Double.parseDouble(formatted_Rat_Value);

                                        //ROUND OF UPTO 2 decimal places
                                        if(converted_distance_from_str_to_doubl > 1)
                                        {
                                            converted_distance_from_str_to_doubl += 1;
                                        }


                                        //ROUND OF UPTO 2 decimal places
                                        //double roundOff = ((double) Math.round(distance * 10) / 10) + 1;
                                        //Toast.makeText(getContext(), "roundOff: " + roundOff, Toast.LENGTH_SHORT).show();

                                        if(marquee_OnClick_sent_name.equals(d.getString("name").toString().trim()))
                                        {
                                            MdistanceValue.setText("");
                                            MdistanceValue.append(String.valueOf(converted_distance_from_str_to_doubl)+" Kms.");
                                        }
                                    }
                                }
                            });
                }
            }
        }); //-------------------------------------------------TvEnd--------------------------------------------//

        //---------------------------------------------------BtnCommentsShowStart/////////////////////////////////
        // comments_show_btn.setOnClickListener(new View.OnClickListener()
        //{
        // @Override
        // public void onClick(View view)
        // {
        //Qeury
        //// Reference to a document in subcollection "messages"
        Task<QuerySnapshot> document = dbroot.collection("Comments").document(marquee_OnClick_sent_email).collection("user_comments")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots)
                        {
                            //Toast.makeText(getContext(), "comment: "+document.getString("comment"), Toast.LENGTH_SHORT).show();
                            //Muser_comments.append("User name: "+ document.getString("username")+" Comment: "+document.getString("comment")+"\n");
                            cmodel obj = document.toObject(cmodel.class);
                            cdatalist.add(obj);
                        }
                        cadapter.notifyDataSetChanged();
                    }
                });
        cdatalist.clear();
        cadapter.notifyDataSetChanged();
        // }
        // });

        /*
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Toast.makeText(getContext(), "comment: "+document.getString("comment"), Toast.LENGTH_SHORT).show();
                                        //NEED RECYCLER VIEW HERE!
                                        Muser_comments.append("User name:"+ document.getString("username")+"\nComment: "+document.getString("comment"));
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
         */

        //------------------------------------------------------------------------------------------------------//

        //LOCATION MARQUEE IMAGEVIEW LISTENER
        location_marker = (ImageView) view.findViewById(R.id.location_marker);

        //qeury for marquee latlong
        if(marquee_lat == 0 && marquee_long == 0) {
            //Toast.makeText(getContext(), "------>latitude: " + marquee_lat, Toast.LENGTH_SHORT).show();

            DocumentReference docRef = dbroot.collection("MarqueeOwners").document(marquee_OnClick_sent_email);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                        {
                            ///convert latitude & longitude number->double
                            Number lat_value = (Number) document.get("latitude");
                            marquee_lat = lat_value.doubleValue();

                            Number long_value = (Number) document.get("longitude");
                            marquee_long = long_value.doubleValue();

                            //Toast.makeText(getContext(), "-->latitude: " + marquee_lat, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        }
                        else
                        {
                            Log.d(TAG, "No such document");
                        }
                    }
                    else
                    {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        location_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(marquee_lat != 0 && marquee_long != 0)
                {
                    //////////////////////////////////////ALERT DIALOGUE
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setMessage("Do you want to use google maps? if yes then tap {yes}\n[Note]:Tap your phone's back button to come back to this application again from the google maps application");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", marquee_lat, marquee_long, "here is your marquee");

                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    intent.setPackage("com.google.android.apps.maps");

                                    try
                                    {
                                        // Launch the Phone app's dialer with a phone
                                        // number to dial a call.
                                        //startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
                                        startActivity(intent);
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
                    //////////////////////////////////////
                    //String uri = "geo:"+ marquee_lat + "," + marquee_long;

                    /*String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", marquee_lat, marquee_long, "here is your marquee");

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");

                    try
                    {
                        // Launch the Phone app's dialer with a phone
                        // number to dial a call.
                        //startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
                        startActivity(intent);
                    }
                    catch (SecurityException s)
                    {
                        // show() method display the toast with
                        // exception message.
                        Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_LONG)
                                .show();
                    }*/
                }
            }
        });
        //

        //=================================================SHOW MARQUEE SERVICES =========================================//
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
                            String service = d.getString("services").toString().trim();
                            Mservices.setText(service);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failure due to technichal issues!");
                    }
                });
        //================================================================================================================//
        return view;
    }//oncreate end

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
            //Toasty.info(getContext(), "Note: location is fetched. \nTo provide you better experience", Toast.LENGTH_SHORT).show();
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


    //Distance using GOOGLE MAPS API
    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2){
        final String[] parsedDistance = new String[1];
        final String[] response = new String[1];
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&key="+"AIzaSyCtBNQm9VIdUolXT82SVXHI6qaCK24PFeQ");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response[0] = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response[0]);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance[0] =distance.getString("text");

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return parsedDistance[0];
    }
    ///////////////////////

}