package com.example.Marqeusina;

import static android.content.ContentValues.TAG;

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

import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OurRecommendations#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OurRecommendations extends Fragment {
    public String user_comments[];

    public boolean check_size = false;

    public  String nlp_stats="";

    public ArrayList<String> cmnts;

    public AImodel datalist_object;

    RecyclerView recview;
    public ArrayList<AImodel> datalist;
    AImyadapter adapter;

    FirebaseFirestore dbroot;
    //
    Map<String,Object> items = new HashMap<>();
    //

    public  PyObject obj;

    //Your recommended results textview
    public TextView results;
    //

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OurRecommendations() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OurRecommendations.
     */
    // TODO: Rename and change types and number of parameters
    public static OurRecommendations newInstance(String param1, String param2) {
        OurRecommendations fragment = new OurRecommendations();
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
        View view = inflater.inflate(R.layout.fragment_our_recommendations, container, false);

        //Your recommended results textview
        results=(TextView)view.findViewById(R.id.results);
        //result=(TextView)view.findViewById(R.id.result);
        //Toast.makeText(getContext(), "Results", Toast.LENGTH_SHORT).show();


        /////////////////////////////////////////////////////////
        recview=(RecyclerView)view.findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        datalist=new ArrayList<>();



        dbroot=FirebaseFirestore.getInstance();

        ////////////////////////////////////////////////////////
        datalist.clear();

        //-----------------------------------------------------PYTHON----------------------------------------------------------//
        if(!Python.isStarted())
            Python.start(new AndroidPlatform(getContext()));

        Python py  = Python.getInstance();
        final PyObject pyobj = py.getModule("test");// here we will give name of our python file
        //PYTHON CODE//---------------------------------------------------------------------------------------------------//

        cmnts = new ArrayList<>();

        final int[] c = {0};
        final int[] cc = {0};
        //---//
        dbroot.collection("Comments")
                //.orderBy("population")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                //doc_id = document.getId();
                                //Toast.makeText(getContext(), "OUTER__docID: "+document.getId(), Toast.LENGTH_SHORT).show();
                                //cmnts = new ArrayList<String>();
                                //##################################################### NESTED QEURY #############################################//
                                dbroot.collection("Comments").document(document.getId()).collection("user_comments")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                                            {
                                                //Toast.makeText(getApplicationContext(), "INNER__docID: "+doc_id, Toast.LENGTH_SHORT).show();
                                                if (task.isSuccessful())
                                                {
                                                    for (QueryDocumentSnapshot d : task.getResult())
                                                    {
                                                        Log.d(TAG, d.getId() + " => " + d.getData());

                                                        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ NLP AI on COMMENTS $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$//
                                                        cmnts.add(d.getString("comment"));
                                                        //user_comments[c[0]] = d.getString("comment");
                                                        //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$//

                                                        if(!d.getString("comment").equals(""))
                                                        {
                                                            datalist_object = d.toObject(AImodel.class);
                                                        }
                                                    }
                                                    user_comments = cmnts.toArray(new String[cmnts.size()]);
                                                    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ NLP AI on COMMENTS $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$//
                                                    try {

                                                        obj = pyobj.callAttrThrows("show_passed_array",(Object)user_comments);
                                                        nlp_stats = obj.toString();

                                                        //Toast.makeText(getContext(), "nlp_stats"+nlp_stats, Toast.LENGTH_SHORT).show();

                                                        if(!nlp_stats.equals("0"))
                                                        {

                                                            check_size = true;

                                                            datalist.add(datalist_object);

                                                            adapter = new AImyadapter(datalist);
                                                            //Toast.makeText(getContext(), "nlp_stats"+nlp_stats, Toast.LENGTH_SHORT).show();
                                                            recview.setAdapter(adapter);
                                                           // if(!adapter.nlp-.equals(""))
                                                           //{
                                                                adapter.notifyDataSetChanged();
                                                                nlp_stats = "";
                                                          //}
                                                        }
                                                        //else
                                                       // {
                                                            //datalist_object = null;
                                                       // }

                                                        if(check_size == false)
                                                        {
                                                            results.setText("Opps! No results found");
                                                        }
                                                        else
                                                        {
                                                            results.setText("Recommended");
                                                        }

                                                    }catch (PyException e){
                                                        Toast.makeText(getContext(), "-->"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    } catch (Throwable throwable) {
                                                        throwable.printStackTrace();
                                                    }
                                                    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$//

                                                }
                                                else
                                                {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });

                                //################################################################################################################//
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return  view;
    }
}