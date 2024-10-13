package com.example.Marqeusina;

import static android.content.ContentValues.TAG;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.protocol.BasicHttpContext;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.protocol.HttpContext;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link filteredMarquees#newInstance} factory method to
 * create an instance of this fragment.
 */
public class filteredMarquees extends Fragment {

    public double roundOff_dis=0;

    RecyclerView recview;
    ArrayList<model> datalist;
    myadapter adapter;

    FirebaseFirestore dbroot;
    //
    Map<String,Object> items = new HashMap<>();
    //

    //Byname
    public String user_clicked_marquee_name="";

    //Byfilter
    public String lat_str="", long_str="", mrating_range="", mhall_price_range="", how_far_in_kms="";
    public double this_far = 0;
    //-------->convert above params to qeury db
    double conv_lat=0.0, conv_long=0.0, conv_rat=0.0, conv_hprice=0.0;

    //Your filtered results textview
    public TextView results;
    //

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBERfname
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public filteredMarquees() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment filteredMarquees.
     */
    // TODO: Rename and change types and number of parameters
    public static filteredMarquees newInstance(String param1, String param2) {
        filteredMarquees fragment = new filteredMarquees();
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
        View view = inflater.inflate(R.layout.fragment_filtered_marquees, container, false);

        //Your filtered results textview
        results=(TextView)view.findViewById(R.id.results);
        //

        //get intent get marquee name to show data accrodingly in this activity
        user_clicked_marquee_name = getArguments().getString("marquee name");
        //Toast.makeText(getContext(), "Clicked marquee name: "+user_clicked_marquee_name, Toast.LENGTH_SHORT).show();

        //get intent get marquee name to show data accrodingly in this activity
        //UTILITY ------------> lat_str="", long_str="", mratings_range="", mhall_price_range=""; of this activity
        //&& home activity ------> //PARAMS UTILITY marquee_lat, marquee_long, ratings_range, hall_price_range;
        //CONVERSION of ABOVE KEYS ---> double conv_lat=0.0, conv_long=0.0, conv_rat=0.0, conv_hprice=0.0;
        lat_str = getArguments().getString("marquee_lat");
        if(lat_str != null)
        { conv_lat = Double.parseDouble(lat_str); }

        long_str = getArguments().getString("marquee_long");
        if(long_str != null)
        { conv_long = Double.parseDouble(long_str); }

        mrating_range = getArguments().getString("rating_range");
        if(mrating_range != null)
        { conv_rat = Double.parseDouble(mrating_range); }

        mhall_price_range = getArguments().getString("hall_price_range");
        if(mhall_price_range != null)
        { conv_hprice = Double.parseDouble(mhall_price_range); }

        how_far_in_kms = getArguments().getString("how_far_in_kms");
        if(how_far_in_kms != null)
        {
            this_far = Double.parseDouble(how_far_in_kms);
        }

        //Toast.makeText(getContext(), "lat_str: "+lat_str, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getContext(), "long_str: "+long_str, Toast.LENGTH_SHORT).show();
       // Toast.makeText(getContext(), "mrating_range: "+mrating_range, Toast.LENGTH_SHORT).show();
       // Toast.makeText(getContext(), "mhall_price_range: "+mhall_price_range, Toast.LENGTH_SHORT).show();

        /////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////
        recview=(RecyclerView)view.findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        datalist=new ArrayList<>();
        adapter=new myadapter(datalist);
        recview.setAdapter(adapter);

        dbroot=FirebaseFirestore.getInstance();

        ////////////////////////////////////////////////////////

        datalist.clear();

        if(lat_str != null && long_str != null && mrating_range != null && mhall_price_range != null)
        {
            //Toast.makeText(getContext(), "All params available", Toast.LENGTH_SHORT).show();
            dbroot.collection("MarqueeOwners").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list)
                            {
                                if (d.exists())
                                {
                                    if (d.get("latitude") != null && d.get("longitude") != null && !d.getString("ratings").equals("") && !d.get("hall_price").equals(""))
                                    {
                                        //for marquees distances [latlongs]
                                        Number lat_value = (Number) d.get("latitude");
                                        Double converted_latval = lat_value.doubleValue();

                                        Number long_value = (Number) d.get("longitude");
                                        Double converted_longval = long_value.doubleValue();

                                        //convert ratings and price
                                        String rat_value = d.getString("ratings");
                                        //convert ratings to double type
                                        double converted_Ratval = Double.parseDouble(rat_value);

                                        Number hall_price = (Number) d.get("hall_price");
                                        Double conv_hall_price = hall_price.doubleValue();
                                        //

                                        //double distance = startPoint.distanceTo(endPoint);
                                        double distance = CalculationsByDistance(conv_lat, conv_long, converted_latval, converted_longval);
                                       // Toast.makeText(getContext(), "distance: " + distance, Toast.LENGTH_SHORT).show();

                                        //ROUND OF UPTO 2 decimal places
                                        if(distance < 1)
                                        {
                                            roundOff_dis = ((double) Math.round(distance * 10) / 10);
                                        }
                                        else
                                        {
                                            roundOff_dis = ((double) Math.round(distance * 10) / 10) + 1;
                                        }

                                        //query
                                        if (roundOff_dis <= this_far && converted_Ratval >= conv_rat && conv_hall_price >= conv_hprice)
                                        {
                                            results.setText("Your Filtered Results: ");
                                            model obj = d.toObject(model.class);
                                            datalist.add(obj);
                                        }
                                    }
                                }
                            }
                            if(datalist.isEmpty())
                            {
                                results.setText("Opps! No results found");
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        } //All PARAMS available condition end
        else  if(lat_str != null && long_str != null && mrating_range == null && mhall_price_range == null)  //only lat long available
        {
            dbroot.collection("MarqueeOwners").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list)
                            {
                                if (d.exists())
                                {
                                    if (d.get("latitude") != null && d.get("longitude") != null)
                                    {
                                        //for marquees distances [latlongs]
                                        Number lat_value = (Number) d.get("latitude");
                                        Double converted_latval = lat_value.doubleValue();

                                        Number long_value = (Number) d.get("longitude");
                                        Double converted_longval = long_value.doubleValue();

                                        //double distance = startPoint.distanceTo(endPoint);
                                        double distance = CalculationsByDistance(conv_lat, conv_long, converted_latval, converted_longval);
                                       // Toast.makeText(getContext(), "distance: " + distance, Toast.LENGTH_SHORT).show();

                                        //ROUND OF UPTO 2 decimal places
                                        if(distance < 1)
                                        {
                                            roundOff_dis = ((double) Math.round(distance * 10) / 10);
                                        }
                                        else
                                        {
                                            roundOff_dis = ((double) Math.round(distance * 10) / 10) + 1;
                                        }

                                        //query
                                        if (roundOff_dis <= this_far)
                                        {
                                            results.setText("Your Filtered Results: ");
                                            model obj = d.toObject(model.class);
                                            datalist.add(obj);
                                        }
                                    }
                                }

                            }
                            if(datalist.isEmpty())
                            {
                                results.setText("Opps! No results found");
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        else if(lat_str == null && long_str == null && mrating_range != null && mhall_price_range != null) //we have no latlong
        {
            dbroot.collection("MarqueeOwners").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list)
                            {

                                if (d.exists())
                                {
                                    if (d.getString("ratings") != null && d.get("hall_price") != null)
                                    {
                                        //convert ratings and price
                                        String rat_value = d.getString("ratings");
                                        //convert ratings to double type
                                        double converted_Ratval = Double.parseDouble(rat_value);

                                        Number hall_price = (Number) d.get("hall_price");
                                        Double conv_hall_price = hall_price.doubleValue();


                                        //query
                                        if (converted_Ratval >= conv_rat && conv_hall_price >= conv_hprice)
                                        {
                                            results.setText("Your Filtered Results: ");
                                            model obj = d.toObject(model.class);
                                            datalist.add(obj);
                                        }
                                       // else
                                       // {
                                       //    results.setText("Opps! No results found");
                                       // }
                                    }

                                }

                            }
                            if(datalist.isEmpty())
                            {
                                results.setText("Opps! No results found");
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        else if(lat_str != null && long_str != null && mrating_range != null && mhall_price_range == null) //only hall price not available/provide
        {
            dbroot.collection("MarqueeOwners").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list)
                            {

                                if (d.exists())
                                {
                                    if (d.get("latitude") != null && d.get("longitude") != null && d.getString("ratings") != null)
                                    {
                                        //for marquees distances [latlongs]
                                        Number lat_value = (Number) d.get("latitude");
                                        Double converted_latval = lat_value.doubleValue();

                                        Number long_value = (Number) d.get("longitude");
                                        Double converted_longval = long_value.doubleValue();

                                        //convert ratings and price
                                        String rat_value = d.getString("ratings");
                                        //convert ratings to double type
                                        double converted_Ratval = Double.parseDouble(rat_value);
                                        //

                                        //double distance = startPoint.distanceTo(endPoint);
                                        double distance = CalculationsByDistance(conv_lat, conv_long, converted_latval, converted_longval);
                                        //Toast.makeText(getContext(), "distance: " + distance, Toast.LENGTH_SHORT).show();

                                        //ROUND OF UPTO 2 decimal places
                                        if(distance < 1)
                                        {
                                            roundOff_dis = ((double) Math.round(distance * 10) / 10);
                                        }
                                        else
                                        {
                                            roundOff_dis = ((double) Math.round(distance * 10) / 10) + 1;
                                        }

                                        //query
                                        if (roundOff_dis <= this_far && converted_Ratval >= conv_rat)
                                        {
                                            results.setText("Your Filtered Results: ");
                                            model obj = d.toObject(model.class);
                                            datalist.add(obj);
                                        }
                                       // else
                                      //  {
                                        //    results.setText("Opps! No results found");
                                       // }
                                    }

                                }
                            }
                            if(datalist.isEmpty())
                            {
                                results.setText("Opps! No results found");
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        else if(lat_str != null && long_str != null && mrating_range == null && mhall_price_range != null) //only ratings not available/provide
        {
            dbroot.collection("MarqueeOwners").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list)
                            {

                                if (d.exists())
                                {
                                    if (d.get("latitude") != null && d.get("longitude") != null && d.get("hall_price") != null)
                                    {
                                        //for marquees distances [latlongs]
                                        Number lat_value = (Number) d.get("latitude");
                                        Double converted_latval = lat_value.doubleValue();

                                        Number long_value = (Number) d.get("longitude");
                                        Double converted_longval = long_value.doubleValue();

                                        //convert price

                                        Number hall_price = (Number) d.get("hall_price");
                                        Double conv_hall_price = hall_price.doubleValue();
                                        //


                                        //double distance = startPoint.distanceTo(endPoint);
                                        double distance = CalculationsByDistance(conv_lat, conv_long, converted_latval, converted_longval);
                                        //Toast.makeText(getContext(), "distance: " + distance, Toast.LENGTH_SHORT).show();

                                        //ROUND OF UPTO 2 decimal places
                                        if(distance < 1)
                                        {
                                            roundOff_dis = ((double) Math.round(distance * 10) / 10);
                                        }
                                        else
                                        {
                                            roundOff_dis = ((double) Math.round(distance * 10) / 10) + 1;
                                        }

                                        //Toast.makeText(getContext(), "roundOff_dis: " + roundOff_dis, Toast.LENGTH_SHORT).show();

                                        //query
                                        if (roundOff_dis <= this_far && conv_hall_price >= conv_hprice)
                                        {
                                            results.setText("Your Filtered Results: ");
                                            model obj = d.toObject(model.class);
                                            datalist.add(obj);
                                        }
                                       // else
                                       // {
                                        //    results.setText("Opps! No results found");
                                        //}
                                    }
                                }

                            }
                            if(datalist.isEmpty())
                            {
                                results.setText("Opps! No results found");
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        else if(lat_str == null && long_str == null && mrating_range != null && mhall_price_range == null) //only ratings provided
        {
            //Toast.makeText(getContext(), "only ratings provided", Toast.LENGTH_SHORT).show();
            dbroot.collection("MarqueeOwners").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list)
                            {

                                if (d.exists())
                                {
                                    if (d.getString("ratings") != null)
                                    {
                                        ///convert ratings
                                        String rat_value = d.getString("ratings");
                                        //convert ratings to double type
                                        double converted_Ratval = Double.parseDouble(rat_value);
                                        //Toast.makeText(getContext(), "converted_Ratval: "+converted_Ratval, Toast.LENGTH_SHORT).show();
                                        //query
                                        if (converted_Ratval >= conv_rat)
                                        {
                                            results.setText("Your Filtered Results: ");
                                            model obj = d.toObject(model.class);
                                            datalist.add(obj);
                                        }
                                    }

                                }

                            }
                            if(datalist.isEmpty())
                            {
                                results.setText("Opps! No results found");
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        else if(lat_str == null && long_str == null && mrating_range == null && mhall_price_range != null) //only hall_price provided
        {
            dbroot.collection("MarqueeOwners").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list)
                            {
                                if (d.exists())
                                {
                                    if (d.get("hall_price") != null)
                                    {
                                        //convert price
                                        Number hall_price = (Number) d.get("hall_price");
                                        Double conv_hall_price = hall_price.doubleValue();
                                        //

                                        //query
                                        if (conv_hall_price >= conv_hprice)
                                        {
                                            results.setText("Your Filtered Results: ");
                                            model obj = d.toObject(model.class);
                                            datalist.add(obj);
                                        }
                                    }
                                }

                            }
                            if(datalist.isEmpty())
                            {
                                results.setText("Opps! No results found");
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }

        datalist.clear();
        adapter.notifyDataSetChanged();
        /////////////////////////////////////////////////////////////////////////////////////////////////////

        //###############################################IFBYNAMESEARCH()###################################################
        if(user_clicked_marquee_name != null)
        {
            dbroot.collection("MarqueeOwners")
                    .whereEqualTo("name",user_clicked_marquee_name)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list)
                            {
                                if (d.exists())
                                {
                                    if (d.get("latitude") != null && d.get("longitude") != null && d.getString("ratings") != null && d.get("hall_price") != null)
                                    {
                                        //if (user_clicked_marquee_name == d.getString("name"))
                                        //{
                                        model obj = d.toObject(model.class);
                                        datalist.add(obj);
                                        //}
                                    }
                                }
                            }
                            if(datalist.isEmpty())
                            {
                                results.setText("Opps! No results found");
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        //###############################################IFBYNAMESEARCH()###################################################
        return view;
    } //CreateView() END

    //Distance function [nearby]***********************************************************
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
    //*************************************************************************************
    //another one----------------------------------------Try also|||||||||||||||||||||||||||||||||||||||||||||||||||
    public void haversine(double lat1, double lon1, double lat2, double lon2) {
        double Rad = 6372.8; //Earth's Radius In kilometers
        // TODO Auto-generated method stub
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double haverdistanceKM = Rad * c;

    }



}